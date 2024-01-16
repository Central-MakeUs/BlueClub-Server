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
                                                    fieldWithPath("nickname").type(STRING).description("닉네임"),
                                                    fieldWithPath("jobTitle").type(STRING).description("직업명"),
                                                    fieldWithPath("jobStart").type(NUMBER).description("직업 시작년도"),
                                                    fieldWithPath("tosAgree").type(BOOLEAN).description("선택약관 동의 여부")
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
                .jobStart(WIZ.getJobStart())
                .tosAgree(WIZ.isTosAgree())
                .build();
    }
}
