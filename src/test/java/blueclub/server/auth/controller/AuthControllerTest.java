package blueclub.server.auth.controller;
/*
import blueclub.server.auth.dto.response.SocialLoginResponse;
import blueclub.server.global.ControllerTest;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static blueclub.server.fixture.JwtTokenFixture.ACCESS_TOKEN;
import static blueclub.server.fixture.JwtTokenFixture.REFRESH_TOKEN;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static javax.management.openmbean.SimpleType.LONG;
import static javax.management.openmbean.SimpleType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Auth [Controller Layer] -> AuthController 테스트")
public class AuthControllerTest extends ControllerTest {
    @Nested
    @DisplayName("소셜 로그인 / 리다이렉트 API [GET /login/{socialType}]")
    class socialLogin {
        private static final String BASE_URL = "/login/{socialType}";
        private static final String SOCIAL_TYPE = "kakao";

        @Test
        @DisplayName("소셜 로그인에 성공한다")
        void socialLoginSuccess() throws Exception {
            // given
            SocialLoginResponse socialLoginResponse = createSocialLoginResponse();
            doNothing()
                    .when(oAuth2LoginSuccessHandler)
                    .onAuthenticationSuccess(any(), any(), any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, SOCIAL_TYPE);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/oauth2/authorization/" + SOCIAL_TYPE))
                    .andExpect(forwardedUrl("/login/oauth2/code/" + SOCIAL_TYPE))
                    .andExpect(jsonPath("$.result.id").value(socialLoginResponse.id()))
                    .andDo(document(
                            "Auth",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("소셜 로그인 API")
                                            .pathParameters(
                                                    parameterWithName("socialType").description("소셜 타입")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(LONG).description("사용자 ID"),
                                                    fieldWithPath("result.accessToken").type(STRING).description("Access Token"),
                                                    fieldWithPath("result.refreshToken").type(STRING).description("Refresh Token")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    private SocialLoginResponse createSocialLoginResponse() {
        return SocialLoginResponse.builder()
                .id(1L)
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }
}
*/