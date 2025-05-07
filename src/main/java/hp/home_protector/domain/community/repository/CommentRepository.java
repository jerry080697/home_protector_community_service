package hp.home_protector.domain.community.repository;

import hp.home_protector.domain.community.entity.Comment;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommentRepository extends MongoRepository<Comment, ObjectId> {
    List<Comment> findByPostId(ObjectId postId, Sort sort);
    long deleteByPostId(ObjectId postId);
}

