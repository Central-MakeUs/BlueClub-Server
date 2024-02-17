package blueclub.server.global.controller;

import blueclub.server.global.ControllerTest;
import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.NULL;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Global Exception [Controller Layer] -> Global Exception 테스트")
public class GlobalExceptionControllerTest extends ControllerTest {

    @Nested
    @DisplayName("Global Exception [GET /health]")
    class tooManyRequest {
        private static final String BASE_URL = "/health";

        @Test
        @DisplayName("API를 0.5초당 2회 이상 호출 시도 시 예외를 반환한다")
        void throwExceptionByTooManyRequest() throws Exception {
            // given

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL);

            // then
            Thread first = new Thread(() -> {
                try {
                    mockMvc.perform(requestBuilder)
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            Thread second = new Thread(() -> {
                try {
                    mockMvc.perform(requestBuilder)
                            .andExpect(status().isTooManyRequests())
                            .andDo(document(
                                    "TooManyRequests",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(
                                            ResourceSnippetParameters.builder()
                                                    .tag("Global Exception")
                                                    .summary("지나치게 잦은 요청(0.5초당 1회 초과) 시 예외")
                                                    .responseFields(
                                                            fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                            fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                            fieldWithPath("result").type(NULL).description("null 반환")
                                                    )
                                                    .responseSchema(Schema.schema("BaseResponse"))
                                                    .build()
                                    )
                            ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            first.start();
            Thread.sleep(0, 1);
            second.start();
        }
    }
}
