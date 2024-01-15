package blueclub.server.auth.controller;

import blueclub.server.auth.dto.request.SocialLoginRequest;
import blueclub.server.auth.dto.response.SocialLoginResponse;
import blueclub.server.global.ControllerTest;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static blueclub.server.fixture.JwtTokenFixture.ACCESS_TOKEN;
import static blueclub.server.fixture.JwtTokenFixture.REFRESH_TOKEN;
import static blueclub.server.fixture.UserFixture.WIZ;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth [Controller Layer] -> AuthController 테스트")
public class AuthControllerTest extends ControllerTest {
    @Nested
    @DisplayName("소셜 로그인 API [POST /auth]")
    class socialLogin {
        private static final String BASE_URL = "/auth";

        @Test
        @DisplayName("1. 소셜 로그인에 성공했으며 신규 회원가입을 진행한다")
        void registerSuccess() throws Exception {
            // given
            SocialLoginResponse socialLoginResponse = registerResponse();
            doReturn(socialLoginResponse)
                    .when(authService)
                    .socialLogin(any(SocialLoginRequest.class));

            // when
            final SocialLoginRequest socialLoginRequest = registerRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(socialLoginRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andDo(document(
                            "socialLoginRegister",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("인증 API - 신규 회원가입 / 기존 사용자 로그인")
                                            .requestFields(
                                                    fieldWithPath("socialId").type(STRING).description("소셜 식별자"),
                                                    fieldWithPath("socialType").type(STRING).description("소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("name").type(STRING).description("이름"),
                                                    fieldWithPath("nickname").type(STRING).description("닉네임"),
                                                    fieldWithPath("email").type(STRING).description("이메일"),
                                                    fieldWithPath("phoneNumber").type(STRING).description("휴대전화번호"),
                                                    fieldWithPath("profileImage").type(STRING).description("프로필 사진 url")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("유저 식별자"),
                                                    fieldWithPath("result.email").type(NULL).description("이메일"),
                                                    fieldWithPath("result.name").type(NULL).description("이름"),
                                                    fieldWithPath("result.nickname").type(STRING).description("닉네임"),
                                                    fieldWithPath("result.phoneNumber").type(NULL).description("휴대전화번호"),
                                                    fieldWithPath("result.profileImage").type(NULL).description("프로필 사진 url"),
                                                    fieldWithPath("result.job").type(NULL).description("직업명"),
                                                    fieldWithPath("result.jobStart").type(NULL).description("직업 시작년도"),
                                                    fieldWithPath("result.tosAgree").type(NULL).description("선택약관 동의여부"),
                                                    fieldWithPath("result.role").type(NULL).description("권한(게스트, 일반 사용자)"),
                                                    fieldWithPath("result.socialType").type(NULL).description("소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("result.socialId").type(NULL).description("소셜 식별자"),
                                                    fieldWithPath("result.accessToken").type(STRING).description("JWT AccessToken"),
                                                    fieldWithPath("result.refreshToken").type(STRING).description("JWT RefreshToken")
                                            )
                                            .requestSchema(Schema.schema("SocialLoginRequest"))
                                            .responseSchema(Schema.schema("SocialLoginResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("2. 소셜 로그인에 성공했으며 기존 사용자의 재로그인을 진행한다")
        void loginSuccess() throws Exception {
            // given
            SocialLoginResponse socialLoginResponse = socialLoginResponse();
            doReturn(socialLoginResponse)
                    .when(authService)
                    .socialLogin(any(SocialLoginRequest.class));

            // when
            final SocialLoginRequest socialLoginRequest = registerRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(socialLoginRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "socialLoginContinue",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("인증 API - 신규 회원가입 / 기존 사용자 로그인")
                                            .requestFields(
                                                    fieldWithPath("socialId").type(STRING).description("소셜 식별자"),
                                                    fieldWithPath("socialType").type(STRING).description("소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("name").type(STRING).description("이름"),
                                                    fieldWithPath("nickname").type(STRING).description("닉네임"),
                                                    fieldWithPath("email").type(STRING).description("이메일"),
                                                    fieldWithPath("phoneNumber").type(STRING).description("휴대전화번호"),
                                                    fieldWithPath("profileImage").type(STRING).description("프로필 사진 url")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("유저 식별자"),
                                                    fieldWithPath("result.email").type(STRING).description("이메일"),
                                                    fieldWithPath("result.name").type(STRING).description("이름"),
                                                    fieldWithPath("result.nickname").type(STRING).description("닉네임"),
                                                    fieldWithPath("result.phoneNumber").type(STRING).description("휴대전화번호"),
                                                    fieldWithPath("result.profileImage").type(STRING).description("프로필 사진 url"),
                                                    fieldWithPath("result.job").type(STRING).description("직업명"),
                                                    fieldWithPath("result.jobStart").type(NUMBER).description("직업 시작년도"),
                                                    fieldWithPath("result.tosAgree").type(BOOLEAN).description("선택약관 동의여부"),
                                                    fieldWithPath("result.role").type(STRING).description("권한(게스트, 일반 사용자)"),
                                                    fieldWithPath("result.socialType").type(STRING).description("소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("result.socialId").type(STRING).description("소셜 식별자"),
                                                    fieldWithPath("result.accessToken").type(STRING).description("JWT AccessToken"),
                                                    fieldWithPath("result.refreshToken").type(STRING).description("JWT RefreshToken")
                                            )
                                            .requestSchema(Schema.schema("SocialLoginRequest"))
                                            .responseSchema(Schema.schema("SocialLoginResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("닉네임 중복체크 API [GET /auth/duplicated]")
    class checkNickname {
        private static final String BASE_URL = "/auth/duplicated";
        private static final String NICKNAME = "wiz";

        @Test
        @DisplayName("닉네임 중복체크에 성공했으며 닉네임이 중복된다")
        void nicknameDuplicated() throws Exception {
            // given
            Boolean isDuplicated = true;
            doReturn(isDuplicated)
                    .when(authService)
                    .checkNickname(anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("nickname", NICKNAME);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isConflict())
                    .andDo(document(
                            "NicknameDuplicated",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("닉네임 중복체크 API")
                                            .queryParameters(
                                                    parameterWithName("nickname").description("중복체크할 닉네임")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .responseSchema(Schema.schema("CheckNicknameResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("닉네임 중복체크에 성공했으며 닉네임이 중복되지 않는다")
        void nicknameNotDuplicated() throws Exception {
            // given
            Boolean isDuplicated = false;
            doReturn(isDuplicated)
                    .when(authService)
                    .checkNickname(anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("nickname", NICKNAME);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "NicknameNotDuplicated",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("닉네임 중복체크 API")
                                            .queryParameters(
                                                    parameterWithName("nickname").description("중복체크할 닉네임")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .responseSchema(Schema.schema("CheckNicknameResponse"))
                                            .build()
                            )
                    ));
        }
    }

    private SocialLoginRequest registerRequest() {
        return SocialLoginRequest.builder()
                .socialId(WIZ.getSocialId())
                .socialType(WIZ.getSocialType().name())
                .name(WIZ.getName())
                .nickname(WIZ.getNickname())
                .email(WIZ.getEmail())
                .phoneNumber(WIZ.getPhoneNumber())
                .profileImage(WIZ.getProfileImage())
                .build();
    }

    private SocialLoginResponse socialLoginResponse() {
        return SocialLoginResponse.builder()
                .id(WIZ.getId())
                .email(WIZ.getEmail())
                .name(WIZ.getName())
                .nickname(WIZ.getNickname())
                .phoneNumber(WIZ.getPhoneNumber())
                .profileImage(WIZ.getProfileImage())
                .job(WIZ.getJob().getTitle())
                .jobStart(WIZ.getJobStart())
                .tosAgree(WIZ.isTosAgree())
                .role(WIZ.getRole().getTitle())
                .socialType(WIZ.getSocialType().name())
                .socialId(WIZ.getSocialId())
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }

    private SocialLoginResponse registerResponse() {
        return SocialLoginResponse.builder()
                .id(WIZ.getId())
                .nickname(WIZ.getNickname())
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }
}