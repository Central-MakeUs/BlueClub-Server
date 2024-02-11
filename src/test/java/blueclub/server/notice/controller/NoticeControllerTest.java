package blueclub.server.notice.controller;

import blueclub.server.global.ControllerTest;
import blueclub.server.notice.dto.request.UpdateNoticeRequest;
import blueclub.server.notice.dto.response.GetNoticeDetailsResponse;
import blueclub.server.notice.dto.response.GetNoticeListResponse;
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
import static blueclub.server.fixture.NoticeFixture.*;
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

@DisplayName("Notice [Controller Layer] -> NoticeController 테스트")
public class NoticeControllerTest extends ControllerTest {
    @Nested
    @DisplayName("공지 작성 API [POST /notice]")
    class addNotice {
        private static final String BASE_URL = "/notice";

        @Test
        @DisplayName("공지 작성에 성공한다")
        void addNoticeSuccess() throws Exception {
            // given
            doNothing()
                    .when(noticeService)
                    .addNotice(any(UpdateNoticeRequest.class));

            // when
            final UpdateNoticeRequest updateNoticeRequest = updateNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .post(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateNoticeRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andDo(document(
                            "AddNotice",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Notice API")
                                            .summary("공지 작성 API")
                                            .requestFields(
                                                    fieldWithPath("title").type(STRING).description("[필수] 제목"),
                                                    fieldWithPath("content").type(STRING).description("[필수] 내용")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .requestSchema(Schema.schema("AddNoticeRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("공지 수정 API [PATCH /notice/{noticeId}]")
    class updateNotice {
        private static final String BASE_URL = "/notice/{noticeId}";

        @Test
        @DisplayName("공지 수정에 성공한다")
        void updateNoticeSuccess() throws Exception {
            // given
            doNothing()
                    .when(noticeService)
                    .updateNotice(anyLong(), any(UpdateNoticeRequest.class));

            // when
            final UpdateNoticeRequest updateNoticeRequest = updateNoticeRequest();
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .patch(BASE_URL, 1)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .contentType(APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateNoticeRequest));

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UpdateNotice",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Notice API")
                                            .summary("공지 수정 API")
                                            .pathParameters(
                                                    parameterWithName("noticeId").description("공지글 ID")
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
                                            .requestSchema(Schema.schema("UpdateNoticeRequest"))
                                            .responseSchema(Schema.schema("BaseResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("공지 삭제 API [DELETE /notice/{noticeId}]")
    class deleteNotice {
        private static final String BASE_URL = "/notice/{noticeId}";

        @Test
        @DisplayName("공지 삭제에 성공한다")
        void deleteNoticeSuccess() throws Exception {
            // given
            doNothing()
                    .when(noticeService)
                    .deleteNotice(anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, 1)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "DeleteNotice",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Notice API")
                                            .summary("공지 삭제 API")
                                            .pathParameters(
                                                    parameterWithName("noticeId").description("공지글 ID")
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
    @DisplayName("공지 리스트 조회 API [GET /notice]")
    class getNoticeList {
        private static final String BASE_URL = "/notice";

        @Test
        @DisplayName("공지 리스트 조회에 성공한다")
        void getNoticeListSuccess() throws Exception {
            // given
            final List<GetNoticeListResponse> getNoticeListResponse = getNoticeListResponse();
            doReturn(getNoticeListResponse)
                    .when(noticeService)
                    .getNoticeList(anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .queryParam("noticeId", "-1")
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetNoticeList",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Notice API")
                                            .summary("공지 리스트 조회 API")
                                            .queryParameters(
                                                    parameterWithName("noticeId").description("마지막 공지글 ID").optional()
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result[].id").type(NUMBER).description("공지글 ID"),
                                                    fieldWithPath("result[].title").type(STRING).description("제목"),
                                                    fieldWithPath("result[].content").type(STRING).description("내용"),
                                                    fieldWithPath("result[].createAt").type(STRING).description("생성일")
                                            )
                                            .responseSchema(Schema.schema("GetNoticeListResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("공지 상세조회 API [GET /notice/{noticeId}]")
    class getNoticeDetails {
        private static final String BASE_URL = "/notice/{noticeId}";

        @Test
        @DisplayName("공지 상세조회에 성공한다")
        void getNoticeDetailsSuccess() throws Exception {
            // given
            final GetNoticeDetailsResponse getNoticeDetailsResponse = getNoticeDetailsResponse();
            doReturn(getNoticeDetailsResponse)
                    .when(noticeService)
                    .getNoticeDetails(anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, 1)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetNoticeDetails",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Notice API")
                                            .summary("공지 상세조회 API")
                                            .pathParameters(
                                                    parameterWithName("noticeId").description("공지글 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.title").type(STRING).description("제목"),
                                                    fieldWithPath("result.content").type(STRING).description("내용"),
                                                    fieldWithPath("result.createAt").type(STRING).description("생성일")
                                            )
                                            .responseSchema(Schema.schema("GetNoticeDetailsResponse"))
                                            .build()
                            )
                    ));
        }
    }

    private UpdateNoticeRequest updateNoticeRequest() {
        return UpdateNoticeRequest.builder()
                .title(FIRST_NOTICE.getTitle())
                .content(FIRST_NOTICE.getContent())
                .build();
    }

    private List<GetNoticeListResponse> getNoticeListResponse() {
        List<GetNoticeListResponse> getNoticeListResponse = new ArrayList<>();
        getNoticeListResponse.add(GetNoticeListResponse.builder()
                        .id(FIFTH_NOTICE.getId())
                        .title(FIFTH_NOTICE.getTitle())
                        .content(FIFTH_NOTICE.getContent())
                        .createAt(FIFTH_NOTICE.getCreateAt())
                .build());
        getNoticeListResponse.add(GetNoticeListResponse.builder()
                .id(FOURTH_NOTICE.getId())
                .title(FOURTH_NOTICE.getTitle())
                .content(FOURTH_NOTICE.getContent())
                .createAt(FOURTH_NOTICE.getCreateAt())
                .build());
        getNoticeListResponse.add(GetNoticeListResponse.builder()
                .id(THIRD_NOTICE.getId())
                .title(THIRD_NOTICE.getTitle())
                .content(THIRD_NOTICE.getContent())
                .createAt(THIRD_NOTICE.getCreateAt())
                .build());
        getNoticeListResponse.add(GetNoticeListResponse.builder()
                .id(SECOND_NOTICE.getId())
                .title(SECOND_NOTICE.getTitle())
                .content(SECOND_NOTICE.getContent())
                .createAt(SECOND_NOTICE.getCreateAt())
                .build());
        return getNoticeListResponse;
    }

    private GetNoticeDetailsResponse getNoticeDetailsResponse() {
        return GetNoticeDetailsResponse.builder()
                .title(FIRST_NOTICE.getTitle())
                .content(FIRST_NOTICE.getContent())
                .createAt(FIRST_NOTICE.getCreateAt())
                .build();
    }
}
