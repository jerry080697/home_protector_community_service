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
    private BoardType boardType;
    private List<String> attachments;  // 이미지 URL 리스트
    private int likeCount;             // 좋아요 수
}
