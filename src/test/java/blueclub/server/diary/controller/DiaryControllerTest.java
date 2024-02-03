package blueclub.server.diary.controller;

import blueclub.server.diary.domain.Rank;
import blueclub.server.diary.dto.request.UpdateCaddyDiaryRequest;
import blueclub.server.diary.dto.response.*;
import blueclub.server.global.ControllerTest;
import blueclub.server.user.domain.Job;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static blueclub.server.fixture.JwtTokenFixture.ACCESS_TOKEN;
import static blueclub.server.fixture.JwtTokenFixture.BEARER;
import static blueclub.server.fixture.UserFixture.WIZ;
import static blueclub.server.fixture.diary.CaddyDiaryFixture.*;
import static blueclub.server.fixture.diary.DayworkerDiaryFixture.DAYWORKER_DIARY;
import static blueclub.server.fixture.diary.RiderDiaryFixture.RIDER_DIARY;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.resourceDetails;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Diary [Controller Layer] -> DiaryController 테스트")
public class DiaryControllerTest extends ControllerTest {

    private static final String JOB = "job";
    private static final String CADDY = "골프 캐디";
    private static final String RIDER = "배달 라이더";
    private static final String DAYWORKER = "일용직 노동자";

    @Nested
    @DisplayName("근무 일지 작성 API [POST /diary]")
    class saveDiary {
        private static final String BASE_URL = "/diary";

        @Test
        @DisplayName("골프 캐디의 근무 일지 작성에 성공한다")
        void saveCaddyDiarySuccess() throws Exception {
            // given
            doReturn(getBoastDiaryResponse())
                    .when(diaryService)
                    .saveCaddyDiary(any(), any(UpdateCaddyDiaryRequest.class), any());

            // when
            final UpdateCaddyDiaryRequest updateCaddyDiaryRequest = updateCaddyDiaryRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("dto", null,
                    "application/json", objectMapper.writeValueAsString(updateCaddyDiaryRequest).getBytes(StandardCharsets.UTF_8));

            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file(file)
                    .file(file)
                    .file(file)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, CADDY);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isCreated())
                    .andDo(document(
                            "SaveCaddyDiary",
                            resourceDetails()
                                    .tag("Diary API")
                                    .summary("근무 일지 작성 API")
                                    .requestSchema(Schema.schema("UpdateCaddyDiaryRequest"))
                                    .responseSchema(Schema.schema("BaseResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParts(
                                    partWithName("dto").description("골프 캐디 근무 일지 내용"),
                                    partWithName("image").description("사진"),
                                    partWithName("image").description("사진"),
                                    partWithName("image").description("사진"),
                                    partWithName("image").description("사진")
                            ),
                            requestPartFields(
                                    "dto",
                                    fieldWithPath("worktype").type(STRING).description("[필수] 근무 형태 (근무, 조퇴, 휴무)"),
                                    fieldWithPath("memo").type(STRING).description("메모"),
                                    fieldWithPath("income").type(NUMBER).description("[필수] 총 수입"),
                                    fieldWithPath("expenditure").type(NUMBER).description("지출액"),
                                    fieldWithPath("saving").type(NUMBER).description("저축액"),
                                    fieldWithPath("date").type(STRING).description("[필수] 근무 일자 // 형식 : yyyy-mm-dd"),
                                    fieldWithPath("imageUrlList").type(ARRAY).description("사진 URL 리스트"),
                                    fieldWithPath("rounding").type(NUMBER).description("[필수] 라운딩 수"),
                                    fieldWithPath("caddyFee").type(NUMBER).description("[필수] 캐디피 수입"),
                                    fieldWithPath("overFee").type(NUMBER).description("오버피 수입"),
                                    fieldWithPath("topdressing").type(BOOLEAN).description("배토 여부")
                            ),
                            responseFields(
                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                    fieldWithPath("result.job").type(STRING).description("직업"),
                                    fieldWithPath("result.workAt").type(STRING).description("근무 날짜"),
                                    fieldWithPath("result.rank").type(STRING).description("근무 순위"),
                                    fieldWithPath("result.income").type(NUMBER).description("총 수입"),
                                    fieldWithPath("result.cases").type(NUMBER).description("총 건수")
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("근무 일지 수정 API [PATCH /diary/{diaryId}]")
    class updateDiary {
        private static final String BASE_URL = "/diary/{diaryId}";

        @Test
        @DisplayName("골프 캐디의 근무 일지 수정에 성공한다")
        void updateCaddyDiarySuccess() throws Exception {
            // given
            doReturn(getBoastDiaryResponse())
                    .when(diaryService)
                    .updateCaddyDiary(any(), anyLong(), any(UpdateCaddyDiaryRequest.class), any());

            // when
            final UpdateCaddyDiaryRequest updateCaddyDiaryRequest = updateCaddyDiaryRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("dto", null,
                    "application/json", objectMapper.writeValueAsString(updateCaddyDiaryRequest).getBytes(StandardCharsets.UTF_8));

            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL, 1)
                    .file(file)
                    .file(file)
                    .file(file)
                    .file(file)
                    .file(mockRequest)
                    .accept(APPLICATION_JSON)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, CADDY);

            requestBuilder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod("PATCH");
                    return request;
                }
            });

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UpdateCaddyDiary",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("근무 일지 수정 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("[필수] 직업명 (골프 캐디, 배달 라이더, 일용직 노동자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.job").type(STRING).description("직업"),
                                                    fieldWithPath("result.workAt").type(STRING).description("근무 날짜"),
                                                    fieldWithPath("result.rank").type(STRING).description("근무 순위"),
                                                    fieldWithPath("result.income").type(NUMBER).description("총 수입"),
                                                    fieldWithPath("result.cases").type(NUMBER).description("총 건수")
                                            )
                                            .responseSchema(Schema.schema("GetBoastDiaryResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("근무 일지 상세조회 API [GET /diary/{diaryId}]")
    class getDiaryDetails {
        private static final String BASE_URL = "/diary/{diaryId}";

        @Test
        @DisplayName("골프 캐디의 근무 일지 상세조회에 성공한다")
        void getCaddyDiaryDetailsSuccess() throws Exception {
            // given
            doReturn(getCaddyDiaryDetailsResponse())
                    .when(diaryService)
                    .getDiaryDetails(any(), anyString(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, 1L)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, CADDY);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetCaddyDiaryDetails",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("근무 일지 상세조회 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 노동자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.memo").type(STRING).description("메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("저축액"),
                                                    fieldWithPath("result.rounding").type(NUMBER).description("라운딩 수"),
                                                    fieldWithPath("result.caddyFee").type(NUMBER).description("캐디피 수입"),
                                                    fieldWithPath("result.overFee").type(NUMBER).description("오버피 수입"),
                                                    fieldWithPath("result.topdressing").type(BOOLEAN).description("배토 여부")
                                            )
                                            .responseSchema(Schema.schema("GetCaddyDiaryDetailsResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("배달 라이더의 근무 일지 상세조회에 성공한다")
        void getRiderDiaryDetailsSuccess() throws Exception {
            // given
            doReturn(getRiderDiaryDetailsResponse())
                    .when(diaryService)
                    .getDiaryDetails(any(), anyString(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, 1L)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, RIDER);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetRiderDiaryDetails",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("근무 일지 상세조회 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 노동자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.memo").type(STRING).description("메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("저축액"),
                                                    fieldWithPath("result.numberOfDeliveries").type(NUMBER).description("배달 건수"),
                                                    fieldWithPath("result.incomeOfDeliveries").type(NUMBER).description("배달 수입"),
                                                    fieldWithPath("result.numberOfPromotions").type(NUMBER).description("프로모션 건수"),
                                                    fieldWithPath("result.incomeOfPromotions").type(NUMBER).description("프로모션 수입")
                                            )
                                            .responseSchema(Schema.schema("GetRiderDiaryDetailsResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("일용직 노동자의 근무 일지 상세조회에 성공한다")
        void getDayworkerDiaryDetailsSuccess() throws Exception {
            // given
            doReturn(getDayworkerDiaryDetailsResponse())
                    .when(diaryService)
                    .getDiaryDetails(any(), anyString(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, 1L)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, DAYWORKER);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetDayworkerDiaryDetails",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("근무 일지 상세조회 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 노동자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.memo").type(STRING).description("메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("저축액"),
                                                    fieldWithPath("result.place").type(STRING).description("현장명"),
                                                    fieldWithPath("result.dailyWage").type(NUMBER).description("일당"),
                                                    fieldWithPath("result.typeOfJob").type(STRING).description("직종"),
                                                    fieldWithPath("result.numberOfWork").type(NUMBER).description("공수")
                                            )
                                            .responseSchema(Schema.schema("GetDayworkerDiaryDetailsResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("근무 일지 삭제 API [DELETE /diary/{diaryId}]")
    class deleteDiary {
        private static final String BASE_URL = "/diary/{diaryId}";

        @Test
        @DisplayName("근무 일지 삭제에 성공한다")
        void deleteDiarySuccess() throws Exception {
            // given
            doNothing()
                    .when(diaryService)
                    .deleteDiary(any(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .delete(BASE_URL, 1L)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(document(
                            "DeleteDiary",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("근무 일지 삭제 API")
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("일자별 근무 정보 조회 API [GET /diary/calendar/{yearMonth}]")
    class getDailyInfo {
        private static final String BASE_URL = "/diary/calendar/{yearMonth}";

        @Test
        @DisplayName("일자별 근무 정보 조회에 성공한다")
        void getDailyInfoSuccess() throws Exception {
            // given
            doReturn(getDailyInfoResponseList())
                    .when(diaryService)
                    .getDailyInfo(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, "2024-01")
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetDailyInfo",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("일자별 근무 정보 조회 API")
                                            .pathParameters(
                                                    parameterWithName("yearMonth").description("년·월 // 형식 : yyyy-mm")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result[].id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result[].date").type(STRING).description("날짜"),
                                                    fieldWithPath("result[].income").type(NUMBER).description("총 수입")
                                            )
                                            .responseSchema(Schema.schema("GetDailyInfoResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("월 최신 근무 내역 조회 API [GET /diary/record/{yearMonth}]")
    class getMonthlyRecord {
        private static final String BASE_URL = "/diary/record/{yearMonth}";

        @Test
        @DisplayName("월 최신 근무 내역 조회에 성공한다")
        void getMonthlyRecordSuccess() throws Exception {
            // given
            doReturn(getMonthlyRecordResponse())
                    .when(diaryService)
                    .getMonthlyRecord(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, "2024-01")
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetMonthlyRecord",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("월 최신 근무 내역 조회 API")
                                            .pathParameters(
                                                    parameterWithName("yearMonth").description("년·월 // 형식 : yyyy-mm")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.totalWorkingDay").type(NUMBER).description("해당 월의 총 근무일"),
                                                    fieldWithPath("result.monthlyRecord[].id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result.monthlyRecord[].date").type(STRING).description("근무 날짜"),
                                                    fieldWithPath("result.monthlyRecord[].worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.monthlyRecord[].income").type(NUMBER).description("총 수입"),
                                                    fieldWithPath("result.monthlyRecord[].numberOfCases").type(NUMBER).description("총 건수")
                                            )
                                            .responseSchema(Schema.schema("GetMonthlyRecordResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("자랑하기 조회 API [GET /diary/boast/{diaryId}]")
    class getBoastDiary {
        private static final String BASE_URL = "/diary/boast/{diaryId}";

        @Test
        @DisplayName("자랑하기 상세조회에 성공한다")
        void getBoastDiarySuccess() throws Exception {
            // given
            doReturn(getBoastDiaryResponse())
                    .when(diaryService)
                    .getBoastDiary(any(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, 1)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetBoastDiaryResponse",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("자랑하기 상세조회 API")
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.job").type(STRING).description("직업"),
                                                    fieldWithPath("result.workAt").type(STRING).description("근무 날짜"),
                                                    fieldWithPath("result.rank").type(STRING).description("근무 순위"),
                                                    fieldWithPath("result.income").type(NUMBER).description("총 수입"),
                                                    fieldWithPath("result.cases").type(NUMBER).description("총 건수")
                                            )
                                            .responseSchema(Schema.schema("GetBoastDiaryResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("일용직 노동자의 자랑하기 상세조회에 성공한다")
        void getDayworkerBoastDiarySuccess() throws Exception {
            // given
            doReturn(getDayworkerBoastDiaryResponse())
                    .when(diaryService)
                    .getBoastDiary(any(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, 1)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetDayworkerBoastDiaryResponse",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("자랑하기 상세조회 API")
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.job").type(STRING).description("직업"),
                                                    fieldWithPath("result.workAt").type(STRING).description("근무 날짜"),
                                                    fieldWithPath("result.rank").type(STRING).description("근무 순위"),
                                                    fieldWithPath("result.income").type(NUMBER).description("총 수입"),
                                                    fieldWithPath("result.cases").type(NULL).description("null 반환")
                                            )
                                            .responseSchema(Schema.schema("GetDayworkerBoastDiaryResponse"))
                                            .build()
                            )
                    ));
        }
    }

    private UpdateCaddyDiaryRequest updateCaddyDiaryRequest() {
        return UpdateCaddyDiaryRequest.builder()
                .worktype(CADDY_DIARY.getWorktype())
                .memo(CADDY_DIARY.getMemo())
                .income(CADDY_DIARY.getIncome())
                .expenditure(CADDY_DIARY.getExpenditure())
                .saving(CADDY_DIARY.getSaving())
                .date(CADDY_DIARY.getDate())
                .imageUrlList(CADDY_DIARY.getImageUrlList())
                .rounding(CADDY_DIARY.getRounding())
                .caddyFee(CADDY_DIARY.getCaddyFee())
                .overFee(CADDY_DIARY.getOverFee())
                .topdressing(CADDY_DIARY.getTopdressing())
                .build();
    }

    private GetCaddyDiaryDetailsResponse getCaddyDiaryDetailsResponse() {
        return GetCaddyDiaryDetailsResponse.builder()
                .worktype(CADDY_DIARY.getWorktype())
                .memo(CADDY_DIARY.getMemo())
                .imageUrlList(CADDY_DIARY.getImageUrlList())
                .income(CADDY_DIARY.getIncome())
                .expenditure(CADDY_DIARY.getExpenditure())
                .saving(CADDY_DIARY.getSaving())
                .rounding(CADDY_DIARY.getRounding())
                .caddyFee(CADDY_DIARY.getCaddyFee())
                .overFee(CADDY_DIARY.getOverFee())
                .topdressing(CADDY_DIARY.getTopdressing())
                .build();
    }

    private GetRiderDiaryDetailsResponse getRiderDiaryDetailsResponse() {
        return GetRiderDiaryDetailsResponse.builder()
                .worktype(RIDER_DIARY.getWorktype())
                .memo(RIDER_DIARY.getMemo())
                .imageUrlList(RIDER_DIARY.getImageUrlList())
                .income(RIDER_DIARY.getIncome())
                .expenditure(RIDER_DIARY.getExpenditure())
                .saving(RIDER_DIARY.getSaving())
                .numberOfDeliveries(RIDER_DIARY.getNumberOfDeliveries())
                .incomeOfDeliveries(RIDER_DIARY.getIncomeOfDeliveries())
                .numberOfPromotions(RIDER_DIARY.getNumberOfPromotions())
                .incomeOfPromotions(RIDER_DIARY.getIncomeOfPromotions())
                .build();
    }

    private GetDayworkerDiaryDetailsResponse getDayworkerDiaryDetailsResponse() {
        return GetDayworkerDiaryDetailsResponse.builder()
                .worktype(DAYWORKER_DIARY.getWorktype())
                .memo(DAYWORKER_DIARY.getMemo())
                .imageUrlList(DAYWORKER_DIARY.getImageUrlList())
                .income(DAYWORKER_DIARY.getIncome())
                .expenditure(DAYWORKER_DIARY.getExpenditure())
                .saving(DAYWORKER_DIARY.getSaving())
                .place(DAYWORKER_DIARY.getPlace())
                .dailyWage(DAYWORKER_DIARY.getDailyWage())
                .typeOfJob(DAYWORKER_DIARY.getTypeOfJob())
                .numberOfWork(DAYWORKER_DIARY.getNumberOfWork())
                .build();
    }

    private List<GetDailyInfoResponse> getDailyInfoResponseList() {
        List<GetDailyInfoResponse> getDailyInfoResponseList = new ArrayList<>();
        getDailyInfoResponseList.add(GetDailyInfoResponse.builder()
                        .id(1L)
                        .date(LocalDate.now().minusDays(1))
                        .income(100000L)
                .build());
        getDailyInfoResponseList.add(GetDailyInfoResponse.builder()
                .id(2L)
                .date(LocalDate.now())
                .income(357000L)
                .build());
        return getDailyInfoResponseList;
    }

    private GetMonthlyRecordResponse getMonthlyRecordResponse() {
        List<MonthlyRecord> monthlyRecordList = new ArrayList<>();
        monthlyRecordList.add(MonthlyRecord.builder()
                .id(CADDY_DIARY.getDiaryId())
                .date(CADDY_DIARY.getDate())
                .worktype(CADDY_DIARY.getWorktype())
                .income(CADDY_DIARY.getIncome())
                .numberOfCases(CADDY_DIARY.getRounding())
                .build());
        monthlyRecordList.add(MonthlyRecord.builder()
                .id(CADDY_DIARY_FOUR.getDiaryId())
                .date(CADDY_DIARY_FOUR.getDate())
                .worktype(CADDY_DIARY_FOUR.getWorktype())
                .income(CADDY_DIARY_FOUR.getIncome())
                .numberOfCases(CADDY_DIARY_FOUR.getRounding())
                .build());
        monthlyRecordList.add(MonthlyRecord.builder()
                .id(CADDY_DIARY_TWO.getDiaryId())
                .date(CADDY_DIARY_TWO.getDate())
                .worktype(CADDY_DIARY_TWO.getWorktype())
                .income(CADDY_DIARY_TWO.getIncome())
                .numberOfCases(CADDY_DIARY_TWO.getRounding())
                .build());
        monthlyRecordList.add(MonthlyRecord.builder()
                .id(CADDY_DIARY_THREE.getDiaryId())
                .date(CADDY_DIARY_THREE.getDate())
                .worktype(CADDY_DIARY_THREE.getWorktype())
                .income(CADDY_DIARY_THREE.getIncome())
                .numberOfCases(CADDY_DIARY_THREE.getRounding())
                .build());

        return GetMonthlyRecordResponse.builder()
                .totalWorkingDay(10)
                .monthlyRecord(monthlyRecordList)
                .build();
    }

    private GetBoastDiaryResponse getBoastDiaryResponse() {
        return GetBoastDiaryResponse.builder()
                .job(WIZ.getJob().getTitle())
                .workAt(CADDY_DIARY.getDate())
                .rank(Rank.UNDER_FIVE_PERCENT.getKey())
                .income(CADDY_DIARY.getIncome())
                .cases(CADDY_DIARY.getRounding())
                .build();
    }

    private GetBoastDiaryResponse getDayworkerBoastDiaryResponse() {
        return GetBoastDiaryResponse.builder()
                .job(Job.DAYWORKER.getTitle())
                .workAt(DAYWORKER_DIARY.getDate())
                .rank(Rank.UNDER_FIVE_PERCENT.getKey())
                .income(DAYWORKER_DIARY.getIncome())
                .build();
    }
}
