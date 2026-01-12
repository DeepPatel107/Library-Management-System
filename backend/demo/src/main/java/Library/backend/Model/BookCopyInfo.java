package Library.backend.Model;

import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "bookCopies")
public class BookCopyInfo {
    @Id
    private ObjectId copyId;

    private ObjectId bookId;     // Reference to main Book
    private boolean isIssued=false;

    private int studentId = -1;       // Who issued it
    private Date issueDate;
    private Date returnDate;

}
