package Library.backend.Model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Component;

@Document
@Getter
@Setter
public class BookUpdateDTO {
    private String title;
    private String author;
    private String description;
    private String imageUrl;
    private Integer copies; // Use wrapper types to allow nulls

    // Getters and setters
}
