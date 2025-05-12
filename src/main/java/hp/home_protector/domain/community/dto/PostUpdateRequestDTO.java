package hp.home_protector.domain.community.dto;

import hp.home_protector.domain.community.entity.BoardType;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequestDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private BoardType boardType;
    private List<String> attachments;
}
