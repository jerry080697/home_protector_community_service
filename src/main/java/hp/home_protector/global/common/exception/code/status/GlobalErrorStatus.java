package hp.home_protector.global.common.exception.code.status;

import hp.home_protector.global.common.exception.code.BaseCodeDto;
import hp.home_protector.global.common.exception.code.BaseCodeInterface;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GlobalErrorStatus implements BaseCodeInterface {
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON402", "Validation Error입니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON404", "요청한 정보를 찾을 수 없습니다."),
    _METHOD_ARGUMENT_ERROR(HttpStatus.BAD_REQUEST, "COMMON405", "Argument Type이 올바르지 않습니다."),
    _INTERNAL_PAGE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "페이지 에러, 0 이상의 페이지를 입력해주세요"),

    // S3 관련 에러
    _S3_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3_5001", "파일 업로드에 실패했습니다."),
    _S3_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3_5002", "파일 삭제에 실패했습니다."),
    _FAILED_NOT_VALID_FORMAT(HttpStatus.BAD_REQUEST, "FILE001","지원하지 않는 파일 확장자입니다."),
    _FAILED_READ_FILE(HttpStatus.BAD_REQUEST, "FILE002","파일을 읽는 중 문제가 발생하였습니다."),

    // For test
    TEMP_EXCEPTION(HttpStatus.BAD_REQUEST, "TEMP4001", "예외처리 테스트입니다."),
    ;

    private final HttpStatus httpStatus;
    private final boolean isSuccess = false;
    private final String code;
    private final String message;

    @Override
    public BaseCodeDto getCode() {
        return BaseCodeDto.builder()
                .httpStatus(httpStatus)
                .isSuccess(isSuccess)
                .code(code)
                .message(message)
                .build();
    }
}
