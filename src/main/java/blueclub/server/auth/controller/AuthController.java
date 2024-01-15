package blueclub.server.auth.controller;

import blueclub.server.auth.dto.request.SocialLoginRequest;
import blueclub.server.auth.dto.response.SocialLoginResponse;
import blueclub.server.auth.service.AuthService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<BaseResponse> socialLogin(@RequestBody SocialLoginRequest socialLoginRequest) {
        SocialLoginResponse socialLoginResponse = authService.socialLogin(socialLoginRequest);
        if (socialLoginResponse.role() == null)
            return BaseResponse.toResponseEntityContainsStatusAndResult(BaseResponseStatus.CREATED, socialLoginResponse);
        return BaseResponse.toResponseEntityContainsResult(socialLoginResponse);
    }

    @GetMapping("/duplicated")
    public ResponseEntity<BaseResponse> checkNickname(@RequestParam("nickname") String nickname) {
        if (authService.checkNickname(nickname))
            throw new BaseException(BaseResponseStatus.DUPLICATED_NICKNAME);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }
}
