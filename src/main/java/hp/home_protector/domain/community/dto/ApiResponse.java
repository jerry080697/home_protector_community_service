package hp.home_protector.domain.community.dto;

import lombok.Getter;
import java.time.Instant;

@Getter
public class ApiResponse<T> {
    private final Instant timestamp = Instant.now();
    private final String code;
    private final String message;
    private final T result;

    public ApiResponse(String code, String message, T result) {
        this.code = code;
        this.message = message;
        this.result = result;
    }
    public static <T> ApiResponse<T> success(String code, String message, T result) {
        return new ApiResponse<>(code, message, result);
    }
}