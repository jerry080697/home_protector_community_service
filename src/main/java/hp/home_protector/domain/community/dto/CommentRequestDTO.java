package hp.home_protector.domain.community.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CommentRequestDTO {
    @NotBlank
    private String content;
}
