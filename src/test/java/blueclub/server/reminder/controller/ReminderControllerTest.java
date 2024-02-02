package blueclub.server.reminder.controller;

import blueclub.server.global.ControllerTest;
import blueclub.server.reminder.dto.request.UpdateReminderRequest;
import blueclub.server.reminder.dto.response.GetReminderListResponse;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.util.ArrayList;
import java.util.List;

import static blueclub.server.fixture.JwtTokenFixture.ACCESS_TOKEN;
import static blueclub.server.fixture.JwtTokenFixture.BEARER;
import static blueclub.server.fixture.ReminderFixture.*;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Reminder [Controller Layer] -> ReminderController 테스트")
public class ReminderControllerTest extends ControllerTest {
    @Nested
    @DisplayName("알림 작성 API [POST /reminder]")
    class addReminder {
        private static final String BASE_URL = "/reminder";

        @Test
        @DisplayName("알림 작성에 성공한다")
        void addReminderSuccess() throws Exception {
            // given
            doNothing()
                    .when(reminderService)
                    .addReminder(any(UpdateReminderRequest.class));

            // when
            final UpdateReminderRequest updateReminderRequest = updateReminderRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReminderRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andDo(document(
                            "AddReminder",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Reminder API")
                                            .summary("알림 작성 API")
                                            .requestFields(
                                                    fieldWithPath("title").type(STRING).description("[필수] 제목"),
                                                    fieldWithPath("content").type(STRING).description("[필수] 내용")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddReminderRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("알림 수정 API [PATCH /reminder/{reminderId}]")
    class updateReminder {
        private static final String BASE_URL = "/reminder/{reminderId}";

        @Test
        @DisplayName("알림 수정에 성공한다")
        void updateReminderSuccess() throws Exception {
            // given
            doNothing()
                    .when(reminderService)
                    .updateReminder(anyLong(), any(UpdateReminderRequest.class));

            // when
            final UpdateReminderRequest updateReminderRequest = updateReminderRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, 1)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateReminderRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UpdateReminder",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Reminder API")
                                            .summary("알림 수정 API")
                                            .pathParameters(
                                                    parameterWithName("reminderId").description("알림 ID")
                                            )
                                            .requestFields(
                                                    fieldWithPath("title").type(STRING).description("[필수] 제목"),
                                                    fieldWithPath("content").type(STRING).description("[필수] 내용")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("UpdateReminderRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("알림 삭제 API [DELETE /reminder/{reminderId}]")
    class deleteReminder {
        private static final String BASE_URL = "/reminder/{reminderId}";

        @Test
        @DisplayName("알림 삭제에 성공한다")
        void deleteReminderSuccess() throws Exception {
            // given
            doNothing()
                    .when(reminderService)
                    .deleteReminder(anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, 1)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(document(
                            "DeleteReminder",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Reminder API")
                                            .summary("알림 삭제 API")
                                            .pathParameters(
                                                    parameterWithName("reminderId").description("알림 ID")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("알림 리스트 조회 API [GET /reminder]")
    class getReminderList {
        private static final String BASE_URL = "/reminder";

        @Test
        @DisplayName("알림 리스트 조회에 성공한다")
        void getReminderListSuccess() throws Exception {
            // given
            final List<GetReminderListResponse> getReminderListResponse = getReminderListResponse();
            doReturn(getReminderListResponse)
                    .when(reminderService)
                    .getReminderList(anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("reminderId", "-1")
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetReminderList",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Reminder API")
                                            .summary("알림 리스트 조회 API")
                                            .queryParameters(
                                                    parameterWithName("reminderId").description("마지막 알림 ID").optional()
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result[].id").type(NUMBER).description("알림 ID"),
                                                    fieldWithPath("result[].title").type(STRING).description("제목"),
                                                    fieldWithPath("result[].content").type(STRING).description("내용"),
                                                    fieldWithPath("result[].createAt").type(STRING).description("생성일")
                                            )
                                            .responseSchema(Schema.schema("GetReminderListResponse"))
                                            .build()
                            )
                    ));
        }
    }

    private UpdateReminderRequest updateReminderRequest() {
        return UpdateReminderRequest.builder()
                .title(FIRST_REMINDER.getTitle())
                .content(FIRST_REMINDER.getContent())
                .build();
    }

    private List<GetReminderListResponse> getReminderListResponse() {
        List<GetReminderListResponse> getReminderListResponse = new ArrayList<>();
        getReminderListResponse.add(GetReminderListResponse.builder()
                        .id(FIFTH_REMINDER.getId())
                        .title(FIFTH_REMINDER.getTitle())
                        .content(FIFTH_REMINDER.getContent())
                        .createAt(FIFTH_REMINDER.getCreateAt())
                .build());
        getReminderListResponse.add(GetReminderListResponse.builder()
                .id(FOURTH_REMINDER.getId())
                .title(FOURTH_REMINDER.getTitle())
                .content(FOURTH_REMINDER.getContent())
                .createAt(FOURTH_REMINDER.getCreateAt())
                .build());
        getReminderListResponse.add(GetReminderListResponse.builder()
                .id(THIRD_REMINDER.getId())
                .title(THIRD_REMINDER.getTitle())
                .content(THIRD_REMINDER.getContent())
                .createAt(THIRD_REMINDER.getCreateAt())
                .build());
        getReminderListResponse.add(GetReminderListResponse.builder()
                .id(SECOND_REMINDER.getId())
                .title(SECOND_REMINDER.getTitle())
                .content(SECOND_REMINDER.getContent())
                .createAt(SECOND_REMINDER.getCreateAt())
                .build());
        return getReminderListResponse;
    }
}
