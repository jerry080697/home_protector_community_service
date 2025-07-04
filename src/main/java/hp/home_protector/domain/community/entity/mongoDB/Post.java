package hp.home_protector.domain.community.entity.mongoDB;

import hp.home_protector.domain.community.entity.BoardType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
@Document("posts")
public class Post {
    @Id
    private ObjectId postId;
    private BoardType boardType;
    @Indexed
    private String userId;
    private String title;
    private String content;
    private List<String> attachments;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
