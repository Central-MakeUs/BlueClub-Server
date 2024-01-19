package blueclub.server.global.response;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseExceptionController {

    /**
     * @valid  유효성 체크에 통과하지 못하면  MethodArgumentNotValidException 이 발생한다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        String validExceptionMessage = getValidExceptionMessage(e.getBindingResult());
        return BaseResponse.toResponseEntityContainsCustomMessage(BaseResponseStatus.INVALID_INPUT_VALUE, validExceptionMessage);
    }

    /**
     * @validated  유효성 체크에 통과하지 못하면  MethodArgumentNotValidException 이 발생한다.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse> constraintViolationException(ConstraintViolationException e) {
        String validExceptionMessage = e.getMessage().split(": ")[1];
        return BaseResponse.toResponseEntityContainsCustomMessage(BaseResponseStatus.INVALID_INPUT_VALUE, validExceptionMessage);
    }

    private String getValidExceptionMessage(BindingResult bindingResult) {
        return bindingResult.getFieldError().getDefaultMessage();
    }
}
