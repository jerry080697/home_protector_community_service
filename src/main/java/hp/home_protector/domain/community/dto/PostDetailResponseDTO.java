package hp.home_protector.domain.community.dto;

import lombok.*;
import hp.home_protector.domain.community.entity.BoardType;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class PostDetailResponseDTO {
    private String postId;
    private String userId;
    private String title;
    private String content;
    private BoardType boardType;
    private List<String> attachments;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponseDTO> comments;
}
