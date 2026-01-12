package Library.backend.Model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Document(collection = "books")
public class Book {
    @Id
    private ObjectId id;

    private String title;
    private String author;
    private String imageUrl;
    private String description;
    private int copies;
    private List<BookCopyInfo> copiesInfo = new ArrayList<>();
}

