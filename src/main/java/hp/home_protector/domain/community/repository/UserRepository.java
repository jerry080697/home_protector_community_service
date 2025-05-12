package hp.home_protector.domain.community.repository;


import hp.home_protector.domain.community.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, ObjectId> {

}
