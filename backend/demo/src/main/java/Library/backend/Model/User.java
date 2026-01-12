package Library.backend.Model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Document(collection = "users")
@Getter
@Setter
public class User {
    @Id
    private ObjectId id;

    private int studentId;

    private String username;
    private String email;
    private String password;
    private String role;
    private List<ObjectId> IssuedBooks = new ArrayList<>();

    public User(int studentId, String password) {
        this.studentId = studentId;
        this.password = password;
        this.role = "USER";  // <- automatically set role during signup
    }
}
