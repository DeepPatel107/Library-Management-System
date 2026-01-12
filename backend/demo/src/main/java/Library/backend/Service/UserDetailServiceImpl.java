package Library.backend.Service;



import Library.backend.Model.User;
import Library.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;



@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String studentIdAsString) throws UsernameNotFoundException {
        int studentId;

        // Parse the studentId safely
        try {
            studentId = Integer.parseInt(studentIdAsString);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid student ID format: " + studentIdAsString);
        }
        System.out.println("UserDetailsService called with studentId: " + studentId);
        User user = userRepository.findByStudentId(studentId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with student ID: " + studentId));

        return org.springframework.security.core.userdetails.User.builder()
                .username(String.valueOf(user.getStudentId()))
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }
}


