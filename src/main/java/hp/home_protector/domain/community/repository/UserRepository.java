package hp.home_protector.domain.community.repository;


import hp.home_protector.domain.community.entity.mongoDB.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByEmail(String email);

}
