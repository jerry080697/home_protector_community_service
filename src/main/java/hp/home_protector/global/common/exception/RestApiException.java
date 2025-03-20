package hp.home_protector.global.common.exception;

import lombok.AllArgsConstructor;
import hp.home_protector.global.common.exception.code.BaseCodeDto;
import hp.home_protector.global.common.exception.code.BaseCodeInterface;


@AllArgsConstructor
public class RestApiException extends RuntimeException {

    private final BaseCodeInterface errorCode; //추상화 시킨 인터페이스를 받아서 사용

    //추상화 시킨 ErrorCode의 getrCode()를 사용하여 ErrorCode를 반환
    public BaseCodeDto getErrorCode() {
        return this.errorCode.getCode();
    }
}
