package blueclub.server.global.response;

import com.slack.api.Slack;
import com.slack.api.model.Attachment;
import com.slack.api.model.Field;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.slack.api.webhook.WebhookPayloads.payload;

@Slf4j
@ControllerAdvice
public class BaseExceptionHandler {

    private final Slack slackClient = Slack.getInstance();

    @Value("${slack.webhook.url}")
    private String webhookUrl;

    /**
     * @valid  유효성 체크에 통과하지 못하면 MethodArgumentNotValidException이 발생한다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String validExceptionMessage = getValidExceptionMessage(e.getBindingResult());
        return BaseResponse.toResponseEntityContainsCustomMessage(BaseResponseStatus.INVALID_INPUT_VALUE, validExceptionMessage);
    }

    /**
     * @validated  유효성 체크에 통과하지 못하면 MethodArgumentNotValidException이 발생한다.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> constraintViolationException(ConstraintViolationException e) {
        String validExceptionMessage = e.getMessage().split(": ")[1];
        return BaseResponse.toResponseEntityContainsCustomMessage(BaseResponseStatus.INVALID_INPUT_VALUE, validExceptionMessage);
    }

    /**
     *   BaseException은 그대로 처리한다.
     */
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<BaseResponse> handleBaseException(BaseException e) {
        return BaseResponse.toResponseEntityContainsStatus(e.errorStatus);
    }

    /**
     *   이외의 Exception은 슬랙과 연동하여 로그에 대한 알림을 받는다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse> exception(Exception e, HttpServletRequest request) {
        sendSlackAlertErrorLog(e, request);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendSlackAlertErrorLog(Exception e, HttpServletRequest request) {
        try {
            slackClient.send(webhookUrl, payload(p -> p
                    .text("Error detected.")
                    .attachments(
                            List.of(generateSlackAttachment(e, request))
                    )
            ));
        } catch (IOException slackError) {
            log.debug("Slack 통신과의 예외 발생");
        }
    }

    private Attachment generateSlackAttachment(Exception e, HttpServletRequest request) {
        String requestTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").format(LocalDateTime.now());
        String xffHeader = request.getHeader("X-FORWARDED-FOR");  // 프록시 서버일 경우 client IP는 여기에 담길 수 있습니다.
        return Attachment.builder()
                .color("ff0000")
                .title(requestTime + " 발생 에러 로그")
                .fields(List.of(
                                generateSlackField("Request IP", xffHeader == null ? request.getRemoteAddr() : xffHeader),
                                generateSlackField("Request URL", request.getRequestURL() + " " + request.getMethod()),
                                generateSlackField("Error Message", e.getMessage())
                        )
                )
                .build();
    }

    private Field generateSlackField(String title, String value) {
        return Field.builder()
                .title(title)
                .value(value)
                .valueShortEnough(false)
                .build();
    }

    private String getValidExceptionMessage(BindingResult bindingResult) {
        return bindingResult.getFieldError().getDefaultMessage();
    }
}
