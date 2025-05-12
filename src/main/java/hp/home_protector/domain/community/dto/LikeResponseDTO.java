package hp.home_protector.domain.community.dto;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class LikeResponseDTO {
    private String postId;
    private String userId;
    private long likeCount;
}
