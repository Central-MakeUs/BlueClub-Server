package blueclub.server.global;

import blueclub.server.auth.config.TestSecurityConfig;
import blueclub.server.auth.controller.AuthController;
import blueclub.server.auth.service.AuthService;
import blueclub.server.diary.controller.DiaryController;
import blueclub.server.diary.service.DiaryService;
import blueclub.server.monthlyGoal.controller.MonthlyGoalController;
import blueclub.server.monthlyGoal.service.MonthlyGoalService;
import blueclub.server.notice.controller.NoticeController;
import blueclub.server.notice.service.NoticeService;
import blueclub.server.reminder.controller.ReminderController;
import blueclub.server.reminder.service.ReminderService;
import blueclub.server.s3.controller.S3Controller;
import blueclub.server.user.controller.UserController;
import blueclub.server.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(value = {
        AuthController.class,
        UserController.class,
        DiaryController.class,
        MonthlyGoalController.class,
        NoticeController.class,
        ReminderController.class,
        S3Controller.class
})
@ExtendWith(RestDocumentationExtension.class)
@Import(TestSecurityConfig.class)
@AutoConfigureRestDocs
public class ControllerTest {
    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @MockBean
    protected AuthService authService;

    @MockBean
    protected UserService userService;

    @MockBean
    protected DiaryService diaryService;

    @MockBean
    protected MonthlyGoalService monthlyGoalService;

    @MockBean
    protected NoticeService noticeService;

    @MockBean
    protected ReminderService reminderService;

    @BeforeEach
    void setUp(WebApplicationContext context, RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }
}
