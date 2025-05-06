package hp.home_protector.domain.community.repository;

import hp.home_protector.domain.community.entity.Like;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LikeRepository extends MongoRepository<Like, ObjectId> {
    boolean existsByUserIdAndPostId(ObjectId userId, ObjectId postId);
    long countByPostId(ObjectId postId);
    void deleteByUserIdAndPostId(ObjectId userId, ObjectId postId);
}
