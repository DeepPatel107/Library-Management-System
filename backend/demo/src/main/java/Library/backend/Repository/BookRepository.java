package Library.backend.Repository;

import Library.backend.Model.Book;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface BookRepository extends MongoRepository<Book, ObjectId> {
    boolean existsByTitle(String title);
    Book findByTitleIgnoreCase(String title);
    void deleteByTitle(String title);
    boolean existsById(ObjectId id);
    List<Book> findByAuthorIgnoreCaseAndIdNot(String author, ObjectId excludeId);
    List<Book> findByTitleContainingIgnoreCaseAndIdNot(String keyword, ObjectId excludeId);
    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByAuthorIgnoreCase(String author);

    Optional<Book> findByTitle(String title);

}
