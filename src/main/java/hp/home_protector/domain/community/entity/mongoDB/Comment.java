package hp.home_protector.domain.community.entity.mongoDB;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@Document("comments")
public class Comment {
    @Id
    private ObjectId commentId;
    @Indexed
    private ObjectId postId;
    private String userId;
    private String content;
    private LocalDateTime createdAt;
}
