package hp.home_protector.domain.community.repository;

import hp.home_protector.domain.community.entity.BoardType;
import hp.home_protector.domain.community.entity.mongoDB.Post;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, ObjectId>{
    List<Post> findByBoardType(BoardType boardType, Sort sort);
    // 추가: 페이징 지원
    Page<Post> findByBoardType(BoardType boardType, Pageable pageable);
}
