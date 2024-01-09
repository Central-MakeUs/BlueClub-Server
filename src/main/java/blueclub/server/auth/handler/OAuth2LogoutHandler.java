package blueclub.server.auth.handler;

import blueclub.server.auth.domain.CustomOAuth2User;
import blueclub.server.auth.service.JwtService;
import blueclub.server.global.response.BaseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static blueclub.server.global.response.BaseResponseStatus.SUCCESS;

@Component
@RequiredArgsConstructor
public class OAuth2LogoutHandler implements LogoutHandler {

    private final JwtService jwtService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
            logoutSuccess(response, oAuth2User);
        } catch (Exception e) {
            try {
                throw e;
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void logoutSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException {
        jwtService.deleteRefreshToken(oAuth2User.getId());

        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        BaseResponse baseResponse = BaseResponse.builder()
                .code(SUCCESS.getCode())
                .message(SUCCESS.getMessage())
                .result(null)
                .build();

        String result = objectMapper.writeValueAsString(baseResponse);
        response.getWriter().write(result);
    }
}
