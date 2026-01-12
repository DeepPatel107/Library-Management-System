package Library.backend.Service;

import Library.backend.Model.Book;
import Library.backend.Model.BookCopyInfo;
import Library.backend.Model.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import Library.backend.Repository.BookRepository;
import Library.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void addUser(User newUser) {
        if (userRepository.existsByStudentId(newUser.getStudentId()))
            throw new IllegalArgumentException("Student ID already exists");

        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        userRepository.save(newUser);
    }

    public Optional<User> getUserByStudentId(int studentId) {
        return userRepository.findByStudentId(studentId);
    }

    public void deleteUserByStudentId(int studentId) {
        userRepository.deleteByStudentId(studentId);
    }

    @Autowired
    private BookRepository bookRepository;
    public List<User> getUsersWhoIssuedBookByTitle(String title) {
        Book book = bookRepository.findByTitleIgnoreCase(title);
        if (book == null) return Collections.emptyList();

        Set<Integer> studentIds = new HashSet<>();
        for (BookCopyInfo copy : book.getCopiesInfo()) {
            if (copy.getStudentId() != -1) {
                studentIds.add(copy.getStudentId());
            }
        }

        return userRepository.findByStudentIdIn(new ArrayList<>(studentIds));
    }

    public boolean resetUserPassword(int studentId, String newPassword) {
        Optional<User> userOpt = userRepository.findByStudentId(studentId);
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        return true;
    }

}
