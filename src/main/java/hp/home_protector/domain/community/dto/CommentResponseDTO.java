package hp.home_protector.domain.community.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommentResponseDTO {
    private String commentId;
    private String userId;
    private String content;
    private LocalDateTime createdAt;
}
