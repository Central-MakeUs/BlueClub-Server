package blueclub.server.file.controller;

import blueclub.server.global.ControllerTest;
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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static blueclub.server.fixture.JwtTokenFixture.ACCESS_TOKEN;
import static blueclub.server.fixture.JwtTokenFixture.BEARER;
import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("File [Controller Layer] -> FileController 테스트")
public class FileControllerTest extends ControllerTest {

    private final String FILE_PATH = "src/test/resources/files/";
    private static final String REQUEST_URL = "http://localhost:8080";

    @Nested
    @DisplayName("프로필 이미지 수정 API [PUT /file/profile]")
    class updateProfileImage {
        private static final String BASE_URL = "/file/profile";

        @Test
        @DisplayName("프로필 이미지 수정에 성공한다")
        void updateProfileImageSuccess() throws Exception {
            // given
            String FILE_NAME = "test.png";
            doNothing()
                    .when(userService)
                    .uploadProfileImage(any(UserDetails.class), any());

            // when
            MockMultipartFile file = createMockMultipartFile("image", FILE_NAME, "image/png");
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .multipart(BASE_URL)
                    .file(file)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            requestBuilder.with(new RequestPostProcessor() {
                @Override
                public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                    request.setMethod("PUT");
                    return request;
                }
            });

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "UpdateProfileImage",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("File Upload API")
                                            .summary("프로필 이미지 수정 API")
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
    @DisplayName("홈 배너 이미지 리스트 조회 API [GET /file/home/banner]")
    class getHomeBanner {
        private static final String BASE_URL = "/file/home/banner";

        @Test
        @DisplayName("홈 배너 이미지 리스트 조회에 성공한다")
        void getHomeBannerSuccess() throws Exception {
            // given
            List<String> response = List.of(
                    REQUEST_URL + "/image/home/banner.png"
            );
            String FILE_NAME = "test.png";
            doReturn(response)
                    .when(fileService)
                    .getHomeBanner(any());

            // when
            MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders
                    .get(BASE_URL)
                    .header(AUTHORIZATION, BEARER, ACCESS_TOKEN);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(document(
                            "GetHomeBannerImage",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(
                                    ResourceSnippetParameters.builder()
                                            .tag("File Upload API")
                                            .summary("홈 배너 이미지 리스트 조회 API")
                                            .responseFields(
                                                    fieldWithPath("code").type(STRING).description("커스텀 상태 코드"),
                                                    fieldWithPath("message").type(STRING).description("커스텀 상태 메시지"),
                                                    fieldWithPath("result[]").type(ARRAY).description("홈 배너 이미지 URL")
                                            )
                                            .responseSchema(Schema.schema("GetHomeBannerImageResponse"))
                                            .build()
                            )
                    ));
        }
    }

    private MockMultipartFile createMockMultipartFile(String dir, String fileName, String contentType) throws IOException {
        try (FileInputStream stream = new FileInputStream(FILE_PATH + fileName)) {
            return new MockMultipartFile(dir, fileName, contentType, stream);
        }
    }
}
