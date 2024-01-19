package blueclub.server.user.controller;

import blueclub.server.global.ControllerTest;
import blueclub.server.user.dto.request.AddDetailsRequest;
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
                    .addUserDetails(any(UserDetails.class), any(AddDetailsRequest.class));

            // when
            final AddDetailsRequest addDetailsRequest = addDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addDetailsRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UserAddDetails",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 추가정보 작성 API")
                                            .requestFields(
                                                    fieldWithPath("nickname").type(STRING).description("[필수] 닉네임"),
                                                    fieldWithPath("jobTitle").type(STRING).description("[필수] 직업명"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입"),
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("[필수] 선택약관 동의 여부")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddDetailsRequest"))
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
            final AddDetailsRequest addDetailsRequest = blankNicknameAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addDetailsRequest));

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
                                                    fieldWithPath("jobTitle").type(STRING).description("[필수] 직업명"),
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
            final AddDetailsRequest addDetailsRequest = OverLengthNicknameAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addDetailsRequest));

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
                                                    fieldWithPath("jobTitle").type(STRING).description("[필수] 직업명"),
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
            final AddDetailsRequest addDetailsRequest = BlankJobTitleAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addDetailsRequest));

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
                                                    fieldWithPath("jobTitle").type(STRING).description("[필수] 직업명"),
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
            final AddDetailsRequest addDetailsRequest = UnderMonthlyTargetIncomeAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addDetailsRequest));

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
                                                    fieldWithPath("jobTitle").type(STRING).description("[필수] 직업명"),
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
        @DisplayName("월 목표 수입이 1000만원 초과이면 회원 추가정보 작성에 실패한다")
        void throwExceptionByOverMonthlyTargetIncome() throws Exception {
            // given
            String validExceptionMessage = "월 수입 목표는 1000만원 이하로 입력해주세요";

            // when
            final AddDetailsRequest addDetailsRequest = OverMonthlyTargetIncomeAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addDetailsRequest));

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
                                                    fieldWithPath("jobTitle").type(STRING).description("[필수] 직업명"),
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
            final AddDetailsRequest addDetailsRequest = NullTosAgreeAddDetailsRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(addDetailsRequest));

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
                                                    fieldWithPath("jobTitle").type(STRING).description("[필수] 직업명"),
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
                    .andExpect(status().isNoContent())
                    .andDo(document(
                            "UserWithdrawal",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("User API")
                                            .summary("회원 탈퇴 API")
                                            .build()
                            )
                    ));
        }
    }

    private AddDetailsRequest addDetailsRequest() {
        return AddDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .jobTitle(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddDetailsRequest blankNicknameAddDetailsRequest() {
        return AddDetailsRequest.builder()
                .nickname("")
                .jobTitle(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddDetailsRequest OverLengthNicknameAddDetailsRequest() {
        return AddDetailsRequest.builder()
                .nickname("ThisIsOverLengthNickname")
                .jobTitle(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddDetailsRequest BlankJobTitleAddDetailsRequest() {
        return AddDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .jobTitle("")
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddDetailsRequest UnderMonthlyTargetIncomeAddDetailsRequest() {
        return AddDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .jobTitle(WIZ.getJob().getTitle())
                .monthlyTargetIncome(99L)
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddDetailsRequest OverMonthlyTargetIncomeAddDetailsRequest() {
        return AddDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .jobTitle(WIZ.getJob().getTitle())
                .monthlyTargetIncome(9999999999L)
                .tosAgree(WIZ.isTosAgree())
                .build();
    }

    private AddDetailsRequest NullTosAgreeAddDetailsRequest() {
        return AddDetailsRequest.builder()
                .nickname(WIZ.getNickname())
                .jobTitle(WIZ.getJob().getTitle())
                .monthlyTargetIncome(WIZ.getMonthlyTargetIncome())
                .tosAgree(null)
                .build();
    }
}
