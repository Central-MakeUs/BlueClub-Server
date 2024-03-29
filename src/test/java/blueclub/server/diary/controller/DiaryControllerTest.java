package blueclub.server.diary.controller;

import blueclub.server.diary.domain.Rank;
import blueclub.server.diary.domain.Worktype;
import blueclub.server.diary.dto.request.UpdateBaseDiaryRequest;
import blueclub.server.diary.dto.request.UpdateCaddyDiaryRequest;
import blueclub.server.diary.dto.response.*;
import blueclub.server.global.ControllerTest;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.domain.Job;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
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
import static org.mockito.Mockito.*;
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
    private static final String DAYWORKER = "일용직 근로자";
    private static final String DATE = "date";
    private static final String TARGET_DATE = "2024-01-25";

    @Nested
    @DisplayName("근무 일지 작성 API [POST /diary]")
    class saveDiary {
        private static final String BASE_URL = "/diary";

        @Test
        @DisplayName("골프 캐디의 근무 일지 작성에 성공한다")
        void saveCaddyDiarySuccess() throws Exception {
            // given
            doReturn(getDiaryIdResponse())
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
                                    .responseSchema(Schema.schema("GetDiaryIdResponse")),
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
                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID")
                            )
                    ));
        }

        @Test
        @DisplayName("휴무일 근무 일지 작성에 성공한다")
        void saveDayOffDiarySuccess() throws Exception {
            // given
            doReturn(getDiaryIdResponse())
                    .when(diaryService)
                    .saveDayOffDiary(any(), any());

            // when
            final UpdateBaseDiaryRequest updateBaseDiaryRequest = updateBaseDiaryRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("dto", null,
                    "application/json", objectMapper.writeValueAsString(updateBaseDiaryRequest).getBytes(StandardCharsets.UTF_8));

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
                            "SaveDayOffDiary",
                            resourceDetails()
                                    .tag("Diary API")
                                    .summary("근무 일지 작성 API")
                                    .requestSchema(Schema.schema("UpdateBaseDiaryRequest"))
                                    .responseSchema(Schema.schema("GetDiaryIdResponse")),
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            requestParts(
                                    partWithName("dto").description("휴무 근무 일지 내용"),
                                    partWithName("image").description("사진"),
                                    partWithName("image").description("사진"),
                                    partWithName("image").description("사진"),
                                    partWithName("image").description("사진")
                            ),
                            requestPartFields(
                                    "dto",
                                    fieldWithPath("worktype").type(STRING).description("[필수] 근무 형태 (근무, 조퇴, 휴무)"),
                                    fieldWithPath("date").type(STRING).description("[필수] 근무 일자 // 형식 : yyyy-mm-dd")
                            ),
                            responseFields(
                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID")
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
            doReturn(getDiaryIdResponse())
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
                                                    parameterWithName("job").description("[필수] 직업명 (골프 캐디, 배달 라이더, 일용직 근로자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID")
                                            )
                                            .responseSchema(Schema.schema("GetDiaryIdResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("휴무일 근무 일지 수정에 성공한다")
        void updateDayOffDiarySuccess() throws Exception {
            // given
            doReturn(getDiaryIdResponse())
                    .when(diaryService)
                    .updateDayOffDiary(any(), anyLong());

            // when
            final UpdateBaseDiaryRequest updateBaseDiaryRequest = updateBaseDiaryRequest();
            MockMultipartFile file = new MockMultipartFile("image", null,
                    "multipart/form-data", new byte[]{});
            MockMultipartFile mockRequest = new MockMultipartFile("dto", null,
                    "application/json", objectMapper.writeValueAsString(updateBaseDiaryRequest).getBytes(StandardCharsets.UTF_8));

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
                            "UpdateDayOffDiary",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("근무 일지 수정 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("[필수] 직업명 (골프 캐디, 배달 라이더, 일용직 근로자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID")
                                            )
                                            .responseSchema(Schema.schema("GetDiaryIdResponse"))
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
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 근로자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.date").type(STRING).description("근무 일자"),
                                                    fieldWithPath("result.memo").type(STRING).description("[DEFAULT NULL] 메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("[DEFAULT 0] 총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("[DEFAULT 0] 지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("[DEFAULT 0] 저축액"),
                                                    fieldWithPath("result.rounding").type(NUMBER).description("라운딩 수"),
                                                    fieldWithPath("result.caddyFee").type(NUMBER).description("캐디피 수입"),
                                                    fieldWithPath("result.overFee").type(NUMBER).description("[DEFAULT 0] 오버피 수입"),
                                                    fieldWithPath("result.topdressing").type(BOOLEAN).description("[DEFAULT FALSE] 배토 여부")
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
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 근로자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.date").type(STRING).description("근무 일자"),
                                                    fieldWithPath("result.memo").type(STRING).description("[DEFAULT NULL] 메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("[DEFAULT 0] 총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("[DEFAULT 0] 지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("[DEFAULT 0] 저축액"),
                                                    fieldWithPath("result.numberOfDeliveries").type(NUMBER).description("배달 건수"),
                                                    fieldWithPath("result.incomeOfDeliveries").type(NUMBER).description("배달 수입"),
                                                    fieldWithPath("result.numberOfPromotions").type(NUMBER).description("[DEFAULT 0] 프로모션 건수"),
                                                    fieldWithPath("result.incomeOfPromotions").type(NUMBER).description("[DEFAULT 0] 프로모션 수입")
                                            )
                                            .responseSchema(Schema.schema("GetRiderDiaryDetailsResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("일용직 근로자의 근무 일지 상세조회에 성공한다")
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
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 근로자)")
                                            )
                                            .pathParameters(
                                                    parameterWithName("diaryId").description("근무 일지 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.date").type(STRING).description("근무 일자"),
                                                    fieldWithPath("result.memo").type(STRING).description("[DEFAULT NULL] 메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("[DEFAULT 0] 총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("[DEFAULT 0] 지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("[DEFAULT 0] 저축액"),
                                                    fieldWithPath("result.place").type(STRING).description("현장명"),
                                                    fieldWithPath("result.dailyWage").type(NUMBER).description("일당"),
                                                    fieldWithPath("result.typeOfJob").type(STRING).description("[DEFAULT NULL] 직종"),
                                                    fieldWithPath("result.numberOfWork").type(NUMBER).description("[DEFAULT 0.0] 공수")
                                            )
                                            .responseSchema(Schema.schema("GetDayworkerDiaryDetailsResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("날짜로 근무 일지 상세조회 API [GET /diary]")
    class getDiaryDetailsByDate {
        private static final String BASE_URL = "/diary";

        @Test
        @DisplayName("골프 캐디의 날짜로 근무 일지 상세조회에 성공한다")
        void getCaddyDiaryDetailsSuccess() throws Exception {
            // given
            doReturn(getCaddyDiaryDetailsResponse())
                    .when(diaryService)
                    .getDiaryDetailsByDate(any(), anyString(), any(LocalDate.class));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, CADDY)
                    .queryParam(DATE, TARGET_DATE);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetCaddyDiaryDetailsByDate",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("날짜로 근무 일지 상세조회 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 근로자)"),
                                                    parameterWithName("date").description("날짜 // 형식 : yyyy-mm-dd")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.date").type(STRING).description("근무 일자"),
                                                    fieldWithPath("result.memo").type(STRING).description("[DEFAULT NULL] 메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("[DEFAULT 0] 총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("[DEFAULT 0] 지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("[DEFAULT 0] 저축액"),
                                                    fieldWithPath("result.rounding").type(NUMBER).description("라운딩 수"),
                                                    fieldWithPath("result.caddyFee").type(NUMBER).description("캐디피 수입"),
                                                    fieldWithPath("result.overFee").type(NUMBER).description("[DEFAULT 0] 오버피 수입"),
                                                    fieldWithPath("result.topdressing").type(BOOLEAN).description("[DEFAULT FALSE] 배토 여부")
                                            )
                                            .responseSchema(Schema.schema("GetCaddyDiaryDetailsResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("배달 라이더의 날짜로 근무 일지 상세조회에 성공한다")
        void getRiderDiaryDetailsSuccess() throws Exception {
            // given
            doReturn(getRiderDiaryDetailsResponse())
                    .when(diaryService)
                    .getDiaryDetailsByDate(any(), anyString(), any(LocalDate.class));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, RIDER)
                    .queryParam(DATE, TARGET_DATE);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetRiderDiaryDetailsByDate",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("날짜로 근무 일지 상세조회 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 근로자)"),
                                                    parameterWithName("date").description("날짜 // 형식 : yyyy-mm-dd")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.date").type(STRING).description("근무 일자"),
                                                    fieldWithPath("result.memo").type(STRING).description("[DEFAULT NULL] 메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("[DEFAULT 0] 총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("[DEFAULT 0] 지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("[DEFAULT 0] 저축액"),
                                                    fieldWithPath("result.numberOfDeliveries").type(NUMBER).description("배달 건수"),
                                                    fieldWithPath("result.incomeOfDeliveries").type(NUMBER).description("배달 수입"),
                                                    fieldWithPath("result.numberOfPromotions").type(NUMBER).description("[DEFAULT 0] 프로모션 건수"),
                                                    fieldWithPath("result.incomeOfPromotions").type(NUMBER).description("[DEFAULT 0] 프로모션 수입")
                                            )
                                            .responseSchema(Schema.schema("GetRiderDiaryDetailsResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("일용직 근로자의 날짜로 근무 일지 상세조회에 성공한다")
        void getDayworkerDiaryDetailsSuccess() throws Exception {
            // given
            doReturn(getDayworkerDiaryDetailsResponse())
                    .when(diaryService)
                    .getDiaryDetailsByDate(any(), anyString(), any(LocalDate.class));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, DAYWORKER)
                    .queryParam(DATE, TARGET_DATE);
            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetDayworkerDiaryDetailsByDate",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("날짜로 근무 일지 상세조회 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 근로자)"),
                                                    parameterWithName("date").description("날짜 // 형식 : yyyy-mm-dd")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result.worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.date").type(STRING).description("근무 일자"),
                                                    fieldWithPath("result.memo").type(STRING).description("[DEFAULT NULL] 메모"),
                                                    fieldWithPath("result.imageUrlList[]").type(ARRAY).description("사진 URL 리스트"),
                                                    fieldWithPath("result.income").type(NUMBER).description("[DEFAULT 0] 총 수입"),
                                                    fieldWithPath("result.expenditure").type(NUMBER).description("[DEFAULT 0] 지출액"),
                                                    fieldWithPath("result.saving").type(NUMBER).description("[DEFAULT 0] 저축액"),
                                                    fieldWithPath("result.place").type(STRING).description("현장명"),
                                                    fieldWithPath("result.dailyWage").type(NUMBER).description("일당"),
                                                    fieldWithPath("result.typeOfJob").type(STRING).description("[DEFAULT NULL] 직종"),
                                                    fieldWithPath("result.numberOfWork").type(NUMBER).description("[DEFAULT 0.0] 공수")
                                            )
                                            .responseSchema(Schema.schema("GetDayworkerDiaryDetailsResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("입력한 날짜의 근무 일지가 없다면 결과로 null을 반환한다")
        void getNotFoundDiaryDetailsSuccess() throws Exception {
            // given
            doReturn(null)
                    .when(diaryService)
                    .getDiaryDetailsByDate(any(), anyString(), any(LocalDate.class));

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN)
                    .queryParam(JOB, CADDY)
                    .queryParam(DATE, TARGET_DATE);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetNotFoundDiaryDetailsByDate",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("날짜로 근무 일지 상세조회 API")
                                            .queryParameters(
                                                    parameterWithName("job").description("직업명 (골프 캐디, 배달 라이더, 일용직 근로자)"),
                                                    parameterWithName("date").description("날짜 // 형식 : yyyy-mm-dd")
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
                    .andExpect(status().isOk())
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
    @DisplayName("월 근무 리스트 조회 API [GET /diary/list/{yearMonth}]")
    class getMonthlyList {
        private static final String BASE_URL = "/diary/list/{yearMonth}";

        @Test
        @DisplayName("월 근무 리스트 조회에 성공한다")
        void getMonthlyListSuccess() throws Exception {
            // given
            doReturn(getMonthlyListResponse())
                    .when(diaryService)
                    .getMonthlyList(any(), any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, "2024-01")
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetMonthlyListResponse",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("월 근무 리스트 조회 API")
                                            .pathParameters(
                                                    parameterWithName("yearMonth").description("년·월 // 형식 : yyyy-mm")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.totalDay").type(NUMBER).description("해당 월의 총 근무일"),
                                                    fieldWithPath("result.monthlyRecord[].id").type(NUMBER).description("근무 일지 ID"),
                                                    fieldWithPath("result.monthlyRecord[].date").type(STRING).description("근무 날짜"),
                                                    fieldWithPath("result.monthlyRecord[].worktype").type(STRING).description("근무 형태"),
                                                    fieldWithPath("result.monthlyRecord[].income").type(NUMBER).description("[DEFAULT 0] 총 수입"),
                                                    fieldWithPath("result.monthlyRecord[].cases").type(NUMBER).description("[DEFAULT NULL] 총 건수 // 휴무, 일용직 근로자일 때 TYPE : NULL")
                                            )
                                            .responseSchema(Schema.schema("GetMonthlyListResponse"))
                                            .build()
                            )
                    ));
        }
    }

    @Nested
    @DisplayName("월 근무 기록 조회 API [GET /diary/record/{yearMonth}]")
    class getMonthlyRecord {
        private static final String BASE_URL = "/diary/record/{yearMonth}";

        @Test
        @DisplayName("월 근무 기록 조회에 성공한다")
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
                            "GetMonthlyRecordResponse",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("Diary API")
                                            .summary("월 근무 기록 조회 API - 홈")
                                            .pathParameters(
                                                    parameterWithName("yearMonth").description("년·월 // 형식 : yyyy-mm")
                                            )
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result.totalDay").type(NUMBER).description("월 총 근무기록일 수"),
                                                    fieldWithPath("result.straightDay").type(NUMBER).description("월 연속 근무기록일 수"),
                                                    fieldWithPath("result.isRenew").type(BOOLEAN).description("기록갱신 여부"),
                                                    fieldWithPath("result.straightMonth").type(NUMBER).description("N달 연속 근무기록"),
                                                    fieldWithPath("result.targetIncome").type(NUMBER).description("[DEFAULT NULL] 월 목표 수입 // 1번도 설정한 적 없을 때 TYPE : NULL"),
                                                    fieldWithPath("result.totalIncome").type(NUMBER).description("[DEFAULT NULL] 월 총 수입 // 1번도 설정한 적 없을 때 TYPE : NULL"),
                                                    fieldWithPath("result.progress").type(NUMBER).description("[DEFAULT NULL] 달성률 (%) // 1번도 설정한 적 없을 때 TYPE : NULL")
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
                                                    fieldWithPath("result.cases").type(NUMBER).description("[DEFAULT NULL] 총 건수 // 일용직 근로자일 때 TYPE : NULL")
                                            )
                                            .responseSchema(Schema.schema("GetBoastDiaryResponse"))
                                            .build()
                            )
                    ));
        }

        @Test
        @DisplayName("일용직 근로자의 자랑하기 상세조회에 성공한다")
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

        @Test
        @DisplayName("휴무일인 근무 일지는 자랑하기 상세조회에 실패한다")
        void throwExceptionByDayOffDiary() throws Exception {
            // given
            doThrow(new BaseException(BaseResponseStatus.CANT_BOAST_DIARY_ERROR))
                    .when(diaryService)
                    .getBoastDiary(any(), anyLong());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL, 1)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andDo(document(
                            "GetBoastDiaryDayOffError",
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
                                                    fieldWithPath("result").type(NULL).description("null 반환")
                                            )
                                            .responseSchema(Schema.schema("BaseResponse"))
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
                .date(CADDY_DIARY.getDate().toString())
                .imageUrlList(CADDY_DIARY.getImageUrlList())
                .rounding(CADDY_DIARY.getRounding())
                .caddyFee(CADDY_DIARY.getCaddyFee())
                .overFee(CADDY_DIARY.getOverFee())
                .topdressing(CADDY_DIARY.getTopdressing())
                .build();
    }

    private UpdateBaseDiaryRequest updateBaseDiaryRequest() {
        return UpdateBaseDiaryRequest.builder()
                .worktype(Worktype.DAY_OFF.getValue())
                .date(CADDY_DIARY.getDate().toString())
                .build();
    }

    private GetCaddyDiaryDetailsResponse getCaddyDiaryDetailsResponse() {
        return GetCaddyDiaryDetailsResponse.builder()
                .id(CADDY_DIARY.getId())
                .worktype(CADDY_DIARY.getWorktype())
                .date(CADDY_DIARY.getDate().toString())
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
                .id(RIDER_DIARY.getId())
                .worktype(RIDER_DIARY.getWorktype())
                .date(RIDER_DIARY.getDate().toString())
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
                .id(DAYWORKER_DIARY.getId())
                .worktype(DAYWORKER_DIARY.getWorktype())
                .date(DAYWORKER_DIARY.getDate().toString())
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

    private GetMonthlyRecordListResponse getMonthlyListResponse() {
        List<MonthlyRecord> monthlyRecordList = new ArrayList<>();
        monthlyRecordList.add(MonthlyRecord.builder()
                .id(CADDY_DIARY.getId())
                .date(CADDY_DIARY.getDate())
                .worktype(CADDY_DIARY.getWorktype())
                .income(CADDY_DIARY.getIncome())
                .cases(CADDY_DIARY.getRounding())
                .build());
        monthlyRecordList.add(MonthlyRecord.builder()
                .id(CADDY_DIARY_FOUR.getId())
                .date(CADDY_DIARY_FOUR.getDate())
                .worktype(CADDY_DIARY_FOUR.getWorktype())
                .income(CADDY_DIARY_FOUR.getIncome())
                .cases(CADDY_DIARY_FOUR.getRounding())
                .build());
        monthlyRecordList.add(MonthlyRecord.builder()
                .id(CADDY_DIARY_TWO.getId())
                .date(CADDY_DIARY_TWO.getDate())
                .worktype(CADDY_DIARY_TWO.getWorktype())
                .income(CADDY_DIARY_TWO.getIncome())
                .cases(CADDY_DIARY_TWO.getRounding())
                .build());
        monthlyRecordList.add(MonthlyRecord.builder()
                .id(CADDY_DIARY_THREE.getId())
                .date(CADDY_DIARY_THREE.getDate())
                .worktype(CADDY_DIARY_THREE.getWorktype())
                .income(CADDY_DIARY_THREE.getIncome())
                .cases(CADDY_DIARY_THREE.getRounding())
                .build());

        return GetMonthlyRecordListResponse.builder()
                .totalDay(10)
                .monthlyRecord(monthlyRecordList)
                .build();
    }

    private GetMonthlyRecordResponse getMonthlyRecordResponse() {
        return GetMonthlyRecordResponse.builder()
                .totalDay(1)
                .straightDay(1)
                .isRenew(true)
                .straightMonth(0)
                .targetIncome(200000L)
                .totalIncome(20000L)
                .progress(10)
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

    private GetDiaryIdResponse getDiaryIdResponse() {
        return new GetDiaryIdResponse(1L);
    }
}
