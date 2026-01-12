package Library.backend.Controller;

import Library.backend.Model.Book;
import Library.backend.Model.User;
import Library.backend.Repository.UserRepository;
import Library.backend.Security.JwtUtil;
import Library.backend.Service.BookService;
import Library.backend.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private BookService bookService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody User user) {
        if (userRepository.findByStudentId(user.getStudentId()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        userService.addUser(user);
        return ResponseEntity.ok("User registered successfully");
    }

    @GetMapping("/similar/{title}")
    public ResponseEntity<List<Book>> getSimilarBooks(@PathVariable String title) {
        System.out.println("Searching for similar books with title: " + title);
        List<Book> similarBooks = bookService.findSimilarBooks(title);
        System.out.println("Found " + similarBooks.size() + " similar books");
        if (similarBooks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(similarBooks);
        }
        return ResponseEntity.ok(similarBooks);
    }



    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("CORS works");
    }



}
