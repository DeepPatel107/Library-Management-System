package Library.backend.Controller;

import Library.backend.Model.Book;
import Library.backend.Model.BookUpdateDTO;
import Library.backend.Model.User;
import Library.backend.Service.BookService;
import Library.backend.Service.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private BookService bookService;

    @Autowired
    private UserService userService;

    @GetMapping("/all-books")
    public ResponseEntity<?> getAllBooks(){
        List<Book> all = bookService.getAll();
        if(all!=null && !all.isEmpty()){
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/title/{bookTitle}")
    public ResponseEntity<?> searchBook(@PathVariable String bookTitle) {
        Book book = bookService.findByTitle(bookTitle);
        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
        return ResponseEntity.ok(book);
    }


    @PutMapping("/update/title/{title}")
    public ResponseEntity<String> partialUpdateBook(
            @PathVariable String title,
            @RequestBody BookUpdateDTO updates) {
        boolean updated = bookService.partialUpdateBook(title, updates);
        if (updated) {
            return ResponseEntity.ok("Book updated successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
        }
    }

    @GetMapping("/users-issued/title/{title}")
    public ResponseEntity<?> getUsersWhoIssuedBook(@PathVariable String title) {
        List<User> users = userService.getUsersWhoIssuedBookByTitle(title);
        if (users.isEmpty()) {
            return ResponseEntity.status(404).body("No users found for given book title.");
        }
        return ResponseEntity.ok(users);
    }


    @DeleteMapping("/delete/title/{title}")
    public ResponseEntity<String> deleteBookByTitle(@PathVariable String title) {
        try {
            boolean deleted = bookService.deleteBook(title);
            if (deleted) {
                return ResponseEntity.status(HttpStatus.OK).body("Book deleted successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete book");
        }
    }

    @GetMapping("/{studentId}")
    public ResponseEntity<?> getUser(@PathVariable int studentId) {
        Optional<User> optionalUser = userService.getUserByStudentId(studentId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            return new ResponseEntity<>(user,HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<?> deleteUser(@PathVariable int studentId) {
        Optional<User> optionalUser = userService.getUserByStudentId(studentId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            userService.deleteUserByStudentId(user.getStudentId());
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/issued-books/{studentId}")
    public ResponseEntity<List<Book>> getIssuedBooks(@PathVariable int studentId) {
        List<Book> books = bookService.getBooksIssuedByStudent(studentId);

        if (!books.isEmpty()) {
            return ResponseEntity.ok(books);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(books);
        }
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


    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addNewBook(@RequestBody Book book) {
        String message = bookService.addNewBook(book);
        return ResponseEntity.ok(Map.of("message", message));
    }

    @PostMapping("/add-multiple")
    public ResponseEntity<Map<String, String>> addMultipleBooks(@RequestBody List<Book> books) {
        String message = bookService.addMultipleBooks(books);
        return ResponseEntity.ok(Map.of("message", message));
    }


    @PostMapping("/{bookId}/addCopies/{copies}")
    public ResponseEntity<Map<String, String>> addCopies(
            @PathVariable ObjectId bookId,
            @PathVariable int copies) {
        String message = bookService.addCopiesToExistingBook(bookId, copies);
        return ResponseEntity.ok(Map.of("message", message));
    }

        @PostMapping("/issue/title/{bookTitle}/to/{studentId}")
        public ResponseEntity<Map<String, String>> issueOrReissueBook(@PathVariable String bookTitle, @PathVariable int studentId) {
            String result = bookService.issueOrReissueBookByTitle(bookTitle, studentId);
            if (result.startsWith("Book issued successfully") || result.startsWith("Book reissued successfully")) {
                return ResponseEntity.ok(Map.of("message", result));
            } else {
                return ResponseEntity.status(409).body(Map.of("error", result));
            }
        }

    @PostMapping("/return/title/{bookTitle}/from/{studentId}")
    public ResponseEntity<Map<String, String>> returnBook(@PathVariable String bookTitle, @PathVariable int studentId) {
        String result = bookService.returnBookByTitle(bookTitle, studentId);
        if (result.equals("Book returned successfully")) {
            return ResponseEntity.ok(Map.of("message", result));
        } else {
            return ResponseEntity.status(409).body(Map.of("error", result));
        }
    }

    @PutMapping("/reset-password/{studentId}")
    public ResponseEntity<String> resetPassword(
            @PathVariable int studentId,
            @RequestBody Map<String, String> request) {

        String newPassword = request.get("newPassword");

        if (newPassword == null || newPassword.isBlank()) {
            return ResponseEntity.badRequest().body("New password must not be blank.");
        }

        boolean success = userService.resetUserPassword(studentId, newPassword);

        if (success) {
            return ResponseEntity.ok("Password reset successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found.");
        }
    }
    @GetMapping("/overdue-books")
    public ResponseEntity<List<Map<String, Object>>> getOverdueBooks() {
        List<Map<String, Object>> overdueBooks = bookService.getOverdueBooks();
        if (overdueBooks.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(overdueBooks);
    }

}
