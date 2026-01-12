package Library.backend.Repository;

import Library.backend.Model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public interface UserRepository extends MongoRepository<User, ObjectId> {

    boolean existsByStudentId(int studentId);
    Optional<User> findByStudentId(int studentId);
    void deleteByStudentId(int studentId);
    List<User> findByStudentIdIn(List<Integer> studentIds);

}
