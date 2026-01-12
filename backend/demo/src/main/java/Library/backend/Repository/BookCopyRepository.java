package Library.backend.Repository;

import Library.backend.Model.Book;
import Library.backend.Model.BookCopyInfo;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface BookCopyRepository extends MongoRepository<BookCopyInfo, ObjectId> {
    List<BookCopyInfo> findByBookId(ObjectId bookId);
    Optional<BookCopyInfo> findFirstByBookIdAndStudentId(ObjectId bookId, Integer studentId);
    Optional<BookCopyInfo> findFirstByBookIdAndStudentIdIsNull(ObjectId bookId); // for finding available copy

    List<BookCopyInfo> findByStudentId(int studentId);


}
