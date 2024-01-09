package blueclub.server.auth.handler;

import blueclub.server.auth.domain.CustomOAuth2User;
import blueclub.server.auth.dto.response.SocialLoginResponse;
import blueclub.server.auth.service.JwtService;
import blueclub.server.global.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static blueclub.server.global.response.BaseResponseStatus.SUCCESS;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtService jwtService;
    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String BEARER = "Bearer ";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            loginSuccess(response, oAuth2User);
        } catch (Exception e) {
            throw e;
        }

    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        String accessToken = jwtService.createAccessToken(oAuth2User.getEmail());
        String refreshToken = jwtService.createRefreshToken();

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        SocialLoginResponse socialLoginResponse = SocialLoginResponse.builder()
                .id(oAuth2User.getId())
                .accessToken(BEARER + accessToken)
                .refreshToken(BEARER + refreshToken)
                .build();
        BaseResponse baseResponse = BaseResponse.builder()
                .code(SUCCESS.getCode())
                .message(SUCCESS.getMessage())
                .result(socialLoginResponse)
                .build();

        String result = objectMapper.writeValueAsString(baseResponse);
        response.getWriter().write(result);

        // jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(oAuth2User.getId(), refreshToken);
    }
}
