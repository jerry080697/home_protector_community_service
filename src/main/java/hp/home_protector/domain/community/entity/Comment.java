package hp.home_protector.domain.community.entity;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

@Document("comments")
public class Comment {
    @Id
    private ObjectId commentId;
    @Indexed
    private ObjectId postId;
    private ObjectId authorId;
    private String content;
    private LocalDateTime createdAt;
}
