package Library.backend.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class AuthRequestDTO {
    private int studentId;
    private String password;
}
