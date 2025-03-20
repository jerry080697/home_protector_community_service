package hp.home_protector.domain.test.controller;

import hp.home_protector.global.common.base.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test")
@Tag(name = "테스트 APi", description = "테스트용 API")
public class TestController {

//    @GetMapping("/get-username")
//    @Operation(summary = "유저 이름 가져오기")
//    public BaseResponse<String> getUserName(
//            @CurrentMember Member member
//    ) {
//        return BaseResponse.onSuccess(member.getName());
//    }

    @GetMapping("")
    @Operation(summary = "그냥 테스트")
    public BaseResponse<String> gettest(
    ) {
        return BaseResponse.onSuccess("테스트 성공!");
    }
}
