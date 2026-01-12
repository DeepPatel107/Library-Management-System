package Library.backend.Controller;

import Library.backend.Model.AuthRequestDTO;
import Library.backend.Model.Book;
import Library.backend.Model.BookCopyInfo;
import Library.backend.Model.User;
import Library.backend.Repository.BookCopyRepository;
import Library.backend.Repository.UserRepository;
import Library.backend.Security.JwtUtil;
import Library.backend.Service.BookService;
import Library.backend.Service.UserService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BookCopyRepository bookCopyRepository;

    @GetMapping("/title/{bookTitle}")
    public ResponseEntity<?> searchBook(@PathVariable String bookTitle) {
        Book book = bookService.findByTitle(bookTitle);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
        return ResponseEntity.ok(book);
    }



    @PostMapping("/signin")
    public ResponseEntity<?> signin(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Missing or invalid Authorization header. Use Basic Auth with studentId:password.");
        }

        try {
            String base64Credentials = authHeader.substring("Basic ".length());
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded);

            // credentials format: "studentId:password"
            String[] values = credentials.split(":", 2);
            if (values.length != 2) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Basic Auth format");
            }

            int studentId = Integer.parseInt(values[0]);
            String password = values[1];

            Optional<User> optionalUser = userRepository.findByStudentId(studentId);
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            User user = optionalUser.get();
            if (!passwordEncoder.matches(password, user.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }

            String token = jwtUtil.generateToken(user.getStudentId());

            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("message", "Login successful");
            response.put("role", user.getRole());
            System.out.println("User Role: " + user.getRole());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to decode Basic Auth header");
        }
    }


    @GetMapping("/issued-books")
    public ResponseEntity<List<Book>> getIssuedBooks(HttpServletRequest request) {
        // Extract ID from token
        System.out.println("hh");
        String authHeader = request.getHeader("Authorization");
        System.out.println("Reached");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.substring(7);
        int studentId;
        try {
            studentId = Integer.parseInt(jwtUtil.extractStudentId(token));
        } catch (JwtException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ArrayList<>());
        }

        List<Book> books = bookService.getBooksIssuedByStudent(studentId);
        if (!books.isEmpty()) {
            return ResponseEntity.ok(books);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(books);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        try {
            String extractedToken = token.replace("Bearer ", "");
            int studentId = Integer.parseInt(jwtUtil.extractStudentId(extractedToken));

            Optional<User> userOptional = userService.getUserByStudentId(studentId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                List<BookCopyInfo> issuedBooks = bookCopyRepository.findByStudentId(studentId);

                Map<String, Object> profileData = new HashMap<>();
                profileData.put("studentId", user.getStudentId());
                profileData.put("username", user.getUsername());
                profileData.put("password", user.getPassword());
                profileData.put("role", user.getRole());
                profileData.put("issuedBooks", issuedBooks);
                profileData.put("email", user.getEmail());


                return ResponseEntity.ok(profileData);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Token");
        }
    }



}
