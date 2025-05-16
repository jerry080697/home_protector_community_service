package hp.home_protector.domain.community.dto;

import hp.home_protector.domain.community.entity.BoardType;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PostResponseDTO {
    private String postId;
    private String userId;
    private String title;
    private String content;
    private BoardType category;
    private List<String> attachments;
    private int likeCount;
}
