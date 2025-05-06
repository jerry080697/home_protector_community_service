package hp.home_protector.domain.community.entity;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.CompoundIndex;

import java.time.LocalDateTime;

@Document("likes")
@Builder
@Getter
@Setter
@CompoundIndex(name = "user_post_idx", def = "{'authorId':1,'postId':1}", unique = true)
public class Like {
    @Id
    private ObjectId likeId;
    private ObjectId userId;
    private ObjectId postId;
    private LocalDateTime createdAt;
}
