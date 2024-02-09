package blueclub.server.auth.controller;

import blueclub.server.auth.dto.request.SocialLoginRequest;
import blueclub.server.auth.dto.response.SocialLoginResponse;
import blueclub.server.auth.service.AuthService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("")
    public ResponseEntity<BaseResponse> socialLogin(
            @Valid @RequestBody SocialLoginRequest socialLoginRequest
    ) {
        SocialLoginResponse socialLoginResponse = authService.socialLogin(socialLoginRequest);
        if (socialLoginResponse.role() == null)
            return BaseResponse.toResponseEntityContainsStatusAndResult(BaseResponseStatus.CREATED, socialLoginResponse);
        return BaseResponse.toResponseEntityContainsResult(socialLoginResponse);
    }

    @GetMapping("/duplicated")
    public ResponseEntity<BaseResponse> checkNickname(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("nickname")
            @NotBlank(message = "닉네임을 입력해주세요")
            @Length(max = 10, message = "닉네임은 10글자 이하로 작성해주세요")
            String nickname
    ) {
        if (authService.checkNickname(userDetails, nickname))
            throw new BaseException(BaseResponseStatus.DUPLICATED_NICKNAME);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @PostMapping("/logout")
    public ResponseEntity<BaseResponse> logout(@AuthenticationPrincipal UserDetails userDetails
    ) {
        authService.logout(userDetails);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }
}
