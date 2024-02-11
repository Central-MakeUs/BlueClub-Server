package blueclub.server.user.controller;

import blueclub.server.global.ControllerTest;
import blueclub.server.user.dto.request.AddUserDetailsRequest;
import blueclub.server.user.dto.request.UpdateAgreementRequest;
import blueclub.server.user.dto.request.UpdateUserDetailsRequest;
import blueclub.server.user.dto.response.GetAgreementResponse;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static blueclub.server.fixture.JwtTokenFixture.ACCESS_TOKEN;
import static blueclub.server.fixture.JwtTokenFixture.BEARER;
import static blueclub.server.fixture.UserFixture.WIZ;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("User [Controller Layer] -> UserController 테스트")
public class UserControllerTest extends ControllerTest {
    @Nested
    @DisplayName("회원 추가정보 작성 API [POST /user/details]")
    class addDetails {
        private static final String BASE_URL = "/user/details";

        @Test
        @DisplayName("회원 추가정보 작성에 성공한다")
        void addDetailsSuccess() throws Exception {
            // given
            doNothing()
                    .when(userService)
                    .addUserDetails(any(UserDetails.class), any(AddUserDetailsRequest.class));

            // when
            final AddUserDetailsRequest addUserDetailsRequest = addDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addUserDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "AddUserDetails",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 작성 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("job").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입"),
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("[필수] 선택약관 동의 여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddUserDetailsRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("닉네임이 입력되지 않으면 회원 추가정보 작성에 실패한다")
        void throwExceptionByBlankNickname() throws Exception {
            // given
            String validExceptionMessage = "닉네임을 입력해주세요";

            // when
            final AddUserDetailsRequest addUserDetailsRequest = blankNicknameAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addUserDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "DetailsBlankNicknameError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 작성 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("job").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입"),
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("[필수] 선택약관 동의 여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddDetailsBlankNicknameErrorRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("닉네임이 10자를 초과하면 회원 추가정보 작성에 실패한다")
        void throwExceptionByOverLengthNickname() throws Exception {
            // given
            String validExceptionMessage = "닉네임은 10글자 이하로 작성해주세요";

            // when
            final AddUserDetailsRequest addUserDetailsRequest = OverLengthNicknameAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addUserDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "DetailsOverLengthNicknameError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 작성 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("job").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입"),
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("[필수] 선택약관 동의 여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddDetailsOverLengthNicknameErrorRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("직업이 입력되지 않으면 회원 추가정보 작성에 실패한다")
        void throwExceptionByBlankJobTitle() throws Exception {
            // given
            String validExceptionMessage = "직업을 선택해주세요";

            // when
            final AddUserDetailsRequest addUserDetailsRequest = BlankJobTitleAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addUserDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "DetailsBlankJobTitleError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 작성 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("job").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입"),
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("[필수] 선택약관 동의 여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddDetailsBlankJobTitleErrorRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("월 목표 수입이 10만원 미만이면 회원 추가정보 작성에 실패한다")
        void throwExceptionByUnderMonthlyTargetIncome() throws Exception {
            // given
            String validExceptionMessage = "월 수입 목표는 10만원 이상으로 입력해주세요";

            // when
            final AddUserDetailsRequest addUserDetailsRequest = UnderMonthlyTargetIncomeAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addUserDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "DetailsUnderMonthlyTargetIncomeError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 작성 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("job").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입"),
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("[필수] 선택약관 동의 여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddDetailsUnderMonthlyTargetIncomeErrorRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("월 목표 수입이 9999만원 초과이면 회원 추가정보 작성에 실패한다")
        void throwExceptionByOverMonthlyTargetIncome() throws Exception {
            // given
            String validExceptionMessage = "월 수입 목표는 9999만원 이하로 입력해주세요";

            // when
            final AddUserDetailsRequest addUserDetailsRequest = OverMonthlyTargetIncomeAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addUserDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "DetailsOverMonthlyTargetIncomeError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 작성 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("job").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입"),
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("[필수] 선택약관 동의 여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddDetailsOverMonthlyTargetIncomeErrorRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("선택약관 동의여부가 입력되지 않면 회원 추가정보 작성에 실패한다")
        void throwExceptionByNullTosAgree() throws Exception {
            // given
            String validExceptionMessage = "선택약관 동의여부를 입력해주세요";

            // when
            final AddUserDetailsRequest addUserDetailsRequest = NullTosAgreeAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addUserDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.message").exists(),
                            jsonPath("$.message").value(validExceptionMessage)
                    )
                    .andDo(document(
                            "DetailsNullTosAgreeError",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 작성 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("job").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입"),
                                                    fieldWithPath("tosAgree").type(NULL).description("[필수] 선택약관 동의 여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddDetailsNullTosAgreeErrorRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("회원 탈퇴 API [DELETE /user/withdrawal]")
    class withdraw {
        private static final String BASE_URL = "/user/withdrawal";

        @Test
        @DisplayName("회원 탈퇴에 성공한다")
        void withdrawSuccess() throws Exception {
            // given
            doNothing()
                    .when(userService)
                    .withdrawUser(any(UserDetails.class));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UserWithdrawal",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 탈퇴 API")
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
    @DisplayName("회원 추가정보 수정 API [PATCH /user/details]")
    class updateUserDetails {
        private static final String BASE_URL = "/user/details";

        @Test
        @DisplayName("회원 추가정보 수정에 성공한다")
        void updateUserDetailsSuccess() throws Exception {
            // given
            doNothing()
                    .when(userService)
                    .updateUserDetails(any(UserDetails.class), any(UpdateUserDetailsRequest.class));

            // when
            final UpdateUserDetailsRequest updateUserDetailsRequest = updateUserDetailsRequest();

            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateUserDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UpdateUserDetails",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 수정 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("job").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 수입 목표")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("UpdateUserDetailsRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("알림 설정 API [POST /user/agreement]")
    class updateAgreement {
        private static final String BASE_URL = "/user/agreement";

        @Test
        @DisplayName("알림 설정에 성공한다")
        void updateAgreementSuccess() throws Exception {
            // given
            doNothing()
                    .when(userService)
                    .updateAgreement(any(UserDetails.class), any(UpdateAgreementRequest.class));

            // when
            final UpdateAgreementRequest updateAgreementRequest = updateAgreementRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateAgreementRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UserUpdateAgreement",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("알림 설정 API")
                                            .requestFields(
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("[필수] 선택약관 동의여부"),
                                                    fieldWithPath("pushAgree").type(BOOLEAN).description("[필수] 푸시알림 동의여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("UpdateAgreementRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("알림 설정 조회 API [GET /user/agreement]")
    class getAgreement {
        private static final String BASE_URL = "/user/agreement";

        @Test
        @DisplayName("알림 설정 조회에 성공한다")
        void getAgreementSuccess() throws Exception {
            // given
            doReturn(getAgreementResponse())
                    .when(userService)
                    .getAgreement(any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UserGetAgreement",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("알림 설정 조회 API")
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.tosAgree").type(BOOLEAN).description("선택약관 동의여부"),
                                                    fieldWithPath("result.pushAgree").type(BOOLEAN).description("[DEFAULT FALSE] 푸시알림 동의여부")
                                            )
                                            .responseSchema(Schema.schema("GetAgreementResponse"))
                                            .build()
                            )
                    ));
        }
    }

    private AddUserDetailsRequest addDetailsRequest() {
        return AddUserDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .job(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddUserDetailsRequest blankNicknameAddDetailsRequest() {
        return AddUserDetailsRequest.builder()
                .nickname("")
                .job(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddUserDetailsRequest OverLengthNicknameAddDetailsRequest() {
        return AddUserDetailsRequest.builder()
                .nickname("ThisIsOverLengthNickname")
                .job(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddUserDetailsRequest BlankJobTitleAddDetailsRequest() {
        return AddUserDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .job("")
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddUserDetailsRequest UnderMonthlyTargetIncomeAddDetailsRequest() {
        return AddUserDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .job(WIZ.getJob().getTitle())
                .monthlyTargetIncome(99L)
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddUserDetailsRequest OverMonthlyTargetIncomeAddDetailsRequest() {
        return AddUserDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .job(WIZ.getJob().getTitle())
                .monthlyTargetIncome(9999999999L)
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddUserDetailsRequest NullTosAgreeAddDetailsRequest() {
        return AddUserDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .job(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(null)
                .build();
    }

    private UpdateAgreementRequest updateAgreementRequest() {
        return UpdateAgreementRequest.builder()
                .tosAgree(true)
                .pushAgree(true)
                .build();
    }

    private UpdateUserDetailsRequest updateUserDetailsRequest() {
        return UpdateUserDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .job(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .build();
    }

    private GetAgreementResponse getAgreementResponse() {
        return GetAgreementResponse.builder()
                .tosAgree(true)
                .pushAgree(false)
                .build();
    }
}
