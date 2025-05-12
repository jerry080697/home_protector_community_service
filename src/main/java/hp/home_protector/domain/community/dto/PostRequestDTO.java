package hp.home_protector.domain.community.dto;


import hp.home_protector.domain.community.entity.BoardType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequestDTO {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private BoardType boardType;
    private List<@NotBlank String> attachments;

}
