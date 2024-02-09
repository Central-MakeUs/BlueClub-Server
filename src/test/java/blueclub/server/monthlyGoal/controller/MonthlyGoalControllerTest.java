package blueclub.server.monthlyGoal.controller;

import blueclub.server.global.ControllerTest;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.monthlyGoal.dto.request.UpdateMonthlyGoalRequest;
import blueclub.server.monthlyGoal.dto.response.GetMonthlyGoalResponse;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.time.YearMonth;

import static blueclub.server.fixture.JwtTokenFixture.ACCESS_TOKEN;
import static blueclub.server.fixture.JwtTokenFixture.BEARER;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("MonthlyGoal [Controller Layer] -> MonthlyGoalController 테스트")
public class MonthlyGoalControllerTest extends ControllerTest {

    @Nested
    @DisplayName("월 목표 수입 작성 및 수정 API [POST /monthly_goal]")
    class updateMonthlyGoal {
        private static final String BASE_URL = "/monthly_goal";

        @Test
        @DisplayName("월 목표 수입 작성 및 수정에 성공한다")
        void updateMonthlyGoalSuccess() throws Exception {
            // given
            doNothing()
                    .when(monthlyGoalService)
                    .updateMonthlyGoal(any(UserDetails.class), any(UpdateMonthlyGoalRequest.class));

            // when
            final UpdateMonthlyGoalRequest updateMonthlyGoalRequest = updateMonthlyGoalRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateMonthlyGoalRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UpdateMonthlyGoal",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("MonthlyGoal API")
                                            .summary("월 목표 수입 작성 및 수정 API")
                                            .requestFields(
                                                    fieldWithPath("yearMonth").type(STRING).description("[필수] 년·월 // 형식 : yyyy-mm"),
                                                    fieldWithPath("monthlyTargetIncome").type(NUMBER).description("[필수] 월 목표 수입")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("UpdateMonthlyGoalRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("월 목표 수입 및 달성률 조회 API [GET /monthly_goal/{yearMonth}]")
    class getMonthlyGoalAndProgress {
        private static final String BASE_URL = "/monthly_goal/{yearMonth}";

        @Test
        @DisplayName("월 목표 수입 및 달성률 조회에 성공한다")
        void getMonthlyGoalAndProgressSuccess() throws Exception {
            // given
            doReturn(getMonthlyGoalResponse())
                    .when(monthlyGoalService)
                    .getMonthlyGoalAndProgress(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, "2024-01")
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetMonthlyGoalAndProgress",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("MonthlyGoal API")
                                            .summary("월 목표 수입 및 달성률 조회 API")
                                            .pathParameters(
                                                    parameterWithName("yearMonth").description("년·월 // 형식 : yyyy-mm")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.targetIncome").type(NUMBER).description("[DEFAULT 0] 월 목표 수입"),
                                                    fieldWithPath("result.totalIncome").type(NUMBER).description("[DEFAULT 0] 월 총 수입"),
                                                    fieldWithPath("result.progress").type(NUMBER).description("[DEFAULT 0] 달성률 (%)")
                                            )
                                            .responseSchema(Schema.schema("GetMonthlyGoalResponse"))
                                            .build()
                            )
                    ));
        }
    }

    private UpdateMonthlyGoalRequest updateMonthlyGoalRequest() {
        return UpdateMonthlyGoalRequest.builder()
                .yearMonth(YearMonth.parse("2024-01"))
                .monthlyTargetIncome(200000L)
                .build();
    }

    private GetMonthlyGoalResponse getMonthlyGoalResponse() {
        return GetMonthlyGoalResponse.builder()
                .targetIncome(200000L)
                .totalIncome(20000L)
                .progress(10)
                .build();
    }
}
