package blueclub.server.auth.controller;

import blueclub.server.auth.domain.Role;
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

import static blueclub.server.fixture.JwtTokenFixture.*;
import static blueclub.server.fixture.UserFixture.WIZ;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Auth [Controller Layer] -> AuthController 테스트")
public class AuthControllerTest extends ControllerTest {
    @Nested
    @DisplayName("소셜 로그인 API [POST /auth]")
    class socialLogin {
        private static final String BASE_URL = "/auth";

        @Test
        @DisplayName("소셜 로그인에 성공했으며 신규 회원가입을 진행한다")
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
                            "SocialLoginRegister",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("인증 API - 신규 회원가입 / 기존 사용자 로그인")
                                            .requestFields(
                                                    fieldWithPath("socialId").type(STRING).description("[필수] 소셜 식별자"),
                                                    fieldWithPath("socialType").type(STRING).description("[필수] 소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("name").type(STRING).description("이름").optional(),
                                                    fieldWithPath("nickname").type(STRING).description("닉네임").optional(),
                                                    fieldWithPath("email").type(STRING).description("이메일").optional(),
                                                    fieldWithPath("phoneNumber").type(STRING).description("휴대전화번호").optional(),
                                                    fieldWithPath("profileImage").type(STRING).description("프로필 사진 url").optional(),
                                                    fieldWithPath("fcmToken").type(STRING).description("FCM Token").optional()
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
                                                    fieldWithPath("result.monthlyTargetIncome").type(NULL).description("월 목표 수입"),
                                                    fieldWithPath("result.tosAgree").type(NULL).description("선택약관 동의여부"),
                                                    fieldWithPath("result.role").type(NULL).description("권한(게스트, 일반 사용자)"),
                                                    fieldWithPath("result.socialType").type(NULL).description("소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("result.socialId").type(NULL).description("소셜 식별자"),
                                                    fieldWithPath("result.accessToken").type(STRING).description("JWT AccessToken"),
                                                    fieldWithPath("result.refreshToken").type(STRING).description("JWT RefreshToken")
                                            )
                                            .requestSchema(Schema.schema("RegisterRequest"))
                                            .responseSchema(Schema.schema("RegisterResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("소셜 로그인에 성공했으며 게스트의 회원가입을 진행한다")
        void registerContinueSuccess() throws Exception {
            // given
            SocialLoginResponse socialLoginResponse = registerContinueResponse();
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
                            "SocialLoginRegisterContinue",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("인증 API - 신규 회원가입 / 기존 사용자 로그인")
                                            .requestFields(
                                                    fieldWithPath("socialId").type(STRING).description("[필수] 소셜 식별자"),
                                                    fieldWithPath("socialType").type(STRING).description("[필수] 소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("name").type(STRING).description("이름").optional(),
                                                    fieldWithPath("nickname").type(STRING).description("닉네임").optional(),
                                                    fieldWithPath("email").type(STRING).description("이메일").optional(),
                                                    fieldWithPath("phoneNumber").type(STRING).description("휴대전화번호").optional(),
                                                    fieldWithPath("profileImage").type(STRING).description("프로필 사진 url").optional(),
                                                    fieldWithPath("fcmToken").type(STRING).description("FCM Token").optional()
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
                                                    fieldWithPath("result.monthlyTargetIncome").type(NULL).description("월 목표 수입"),
                                                    fieldWithPath("result.tosAgree").type(NULL).description("선택약관 동의여부"),
                                                    fieldWithPath("result.role").type(STRING).description("권한(게스트, 일반 사용자)"),
                                                    fieldWithPath("result.socialType").type(NULL).description("소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("result.socialId").type(NULL).description("소셜 식별자"),
                                                    fieldWithPath("result.accessToken").type(STRING).description("JWT AccessToken"),
                                                    fieldWithPath("result.refreshToken").type(STRING).description("JWT RefreshToken")
                                            )
                                            .requestSchema(Schema.schema("RegisterContinueRequest"))
                                            .responseSchema(Schema.schema("RegisterContinueResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("소셜 로그인에 성공했으며 기존 사용자의 재로그인을 진행한다")
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
                            "SocialLoginContinue",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("인증 API - 신규 회원가입 / 기존 사용자 로그인")
                                            .requestFields(
                                                    fieldWithPath("socialId").type(STRING).description("[필수] 소셜 식별자"),
                                                    fieldWithPath("socialType").type(STRING).description("[필수] 소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("name").type(STRING).description("이름").optional(),
                                                    fieldWithPath("nickname").type(STRING).description("닉네임").optional(),
                                                    fieldWithPath("email").type(STRING).description("이메일").optional(),
                                                    fieldWithPath("phoneNumber").type(STRING).description("휴대전화번호").optional(),
                                                    fieldWithPath("profileImage").type(STRING).description("프로필 사진 url").optional(),
                                                    fieldWithPath("fcmToken").type(STRING).description("FCM Token").optional()
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
                                                    fieldWithPath("result.monthlyTargetIncome").type(NUMBER).description("월 목표 수입"),
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

        @Test
        @DisplayName("소셜 식별자가 존재하지 않으면 소셜 로그인에 실패한다")
        void throwExceptionByBlankSocialId() throws Exception {
            // given
            String validExceptionMessage = "소셜 식별자(ID)는 필수입니다";

            // when
            final SocialLoginRequest socialLoginRequest = blankSocialIdRegisterRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(socialLoginRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "AuthBlankSocialIdError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("인증 API - 신규 회원가입 / 기존 사용자 로그인")
                                            .requestFields(
                                                    fieldWithPath("socialId").type(STRING).description("[필수] 소셜 식별자"),
                                                    fieldWithPath("socialType").type(STRING).description("[필수] 소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("name").type(STRING).description("이름").optional(),
                                                    fieldWithPath("nickname").type(STRING).description("닉네임").optional(),
                                                    fieldWithPath("email").type(STRING).description("이메일").optional(),
                                                    fieldWithPath("phoneNumber").type(STRING).description("휴대전화번호").optional(),
                                                    fieldWithPath("profileImage").type(STRING).description("프로필 사진 url").optional(),
                                                    fieldWithPath("fcmToken").type(STRING).description("FCM Token").optional()
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("NULL 반환")
                                            )
                                            .requestSchema(Schema.schema("BlankSocialIdErrorRequest"))
                                            .responseSchema(Schema.schema("BlankSocialIdErrorResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("소셜 타입이 존재하지 않으면 소셜 로그인에 실패한다")
        void throwExceptionByBlankSocialType() throws Exception {
            // given
            String validExceptionMessage = "소셜 타입(kakao, naver, apple)은 필수입니다";

            // when
            final SocialLoginRequest socialLoginRequest = blankSocialTypeRegisterRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(socialLoginRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "AuthBlankSocialTypeError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("인증 API - 신규 회원가입 / 기존 사용자 로그인")
                                            .requestFields(
                                                    fieldWithPath("socialId").type(STRING).description("[필수] 소셜 식별자"),
                                                    fieldWithPath("socialType").type(STRING).description("[필수] 소셜 타입 (kakao, naver, apple)"),
                                                    fieldWithPath("name").type(STRING).description("이름").optional(),
                                                    fieldWithPath("nickname").type(STRING).description("닉네임").optional(),
                                                    fieldWithPath("email").type(STRING).description("이메일").optional(),
                                                    fieldWithPath("phoneNumber").type(STRING).description("휴대전화번호").optional(),
                                                    fieldWithPath("profileImage").type(STRING).description("프로필 사진 url").optional(),
                                                    fieldWithPath("fcmToken").type(STRING).description("FCM Token").optional()
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("NULL 반환")
                                            )
                                            .requestSchema(Schema.schema("BlankSocialTypeErrorRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
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
                    .checkNickname(any(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("nickname", NICKNAME)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

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
                                                    parameterWithName("nickname").description("[필수] 중복체크할 닉네임")
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
                    .checkNickname(any(), anyString());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("nickname", NICKNAME)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

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
                                                    parameterWithName("nickname").description("[필수] 중복체크할 닉네임")
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
        @DisplayName("닉네임을 입력하지 않으면 닉네임 중복체크에 실패한다")
        void throwExceptionByBlankNickname() throws Exception {
            // given
            String validExceptionMessage = "닉네임을 입력해주세요";
            String BLANK_NICKNAME = "";

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("nickname", BLANK_NICKNAME)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "DuplicatedBlankNicknameError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("닉네임 중복체크 API")
                                            .queryParameters(
                                                    parameterWithName("nickname").description("[필수] 중복체크할 닉네임")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("닉네임을 10자를 초과하면 닉네임 중복체크에 실패한다")
        void throwExceptionByOverLengthNickname() throws Exception {
            // given
            String validExceptionMessage = "닉네임은 10글자 이하로 작성해주세요";
            String OVER_LENGTH_NICKNAME = "ThisIsOverLengthNickname";

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("nickname", OVER_LENGTH_NICKNAME)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "DuplicatedOverLengthNicknameError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("닉네임 중복체크 API")
                                            .queryParameters(
                                                    parameterWithName("nickname").description("[필수] 중복체크할 닉네임")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("로그아웃 API [GET /auth/logout]")
    class logout {
        private static final String BASE_URL = "/auth/logout";

        @Test
        @DisplayName("로그아웃에 성공한다")
        void logoutSuccess() throws Exception {
            // given
            doNothing()
                    .when(authService)
                    .logout(any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "Logout",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Auth API")
                                            .summary("로그아웃 API")
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .responseSchema(Schema.schema("BaseResponse"))
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

    private SocialLoginRequest blankSocialIdRegisterRequest() {
        return SocialLoginRequest.builder()
                .socialId("")
                .socialType(WIZ.getSocialType().name())
                .name(WIZ.getName())
                .nickname(WIZ.getNickname())
                .email(WIZ.getEmail())
                .phoneNumber(WIZ.getPhoneNumber())
                .profileImage(WIZ.getProfileImage())
                .build();
    }

    private SocialLoginRequest blankSocialTypeRegisterRequest() {
        return SocialLoginRequest.builder()
                .socialId(WIZ.getSocialId())
                .socialType("")
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
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
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

    private SocialLoginResponse registerContinueResponse() {
        return SocialLoginResponse.builder()
                .id(WIZ.getId())
                .nickname(WIZ.getNickname())
                .role(Role.GUEST.getTitle())
                .accessToken(ACCESS_TOKEN)
                .refreshToken(REFRESH_TOKEN)
                .build();
    }
}