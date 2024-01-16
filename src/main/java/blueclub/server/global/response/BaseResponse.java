package blueclub.server.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

import static blueclub.server.global.response.BaseResponseStatus.SUCCESS;

@Data
@Builder
@JsonPropertyOrder({"code", "message", "result"})
public class BaseResponse {
    private final String code;
    private final String message;
    @JsonInclude(JsonInclude.Include.ALWAYS)
    private Object result;

    // Http Status만 Response로 반환하는 경우 (DELETE)
    public static ResponseEntity<BaseResponse> toResponseEntity(BaseResponseStatus baseResponseStatus) {
        return ResponseEntity
                .status(baseResponseStatus.getStatus())
                .body(null);
    }

    // Http Status만 Response로 반환하는 경우
    public static ResponseEntity<BaseResponse> toResponseEntityContainsStatus(BaseResponseStatus baseResponseStatus) {
        return ResponseEntity
                .status(baseResponseStatus.getStatus())
                .body(BaseResponse.builder()
                        .code(baseResponseStatus.getCode())
                        .message(baseResponseStatus.getMessage())
                        .result(null)
                        .build());
    }

    // Http Status 200, Result가 존재하는 Response로 반환하는 경우
    public static ResponseEntity<BaseResponse> toResponseEntityContainsResult(Object result) {
        return ResponseEntity
                .status(SUCCESS.getStatus())
                .body(BaseResponse.builder()
                        .code(SUCCESS.getCode())
                        .message(SUCCESS.getMessage())
                        .result(result)
                        .build());
    }

    // 200 이외의 Status, Result가 존재하는 Response로 반환하는 경우
    public static ResponseEntity<BaseResponse> toResponseEntityContainsStatusAndResult(BaseResponseStatus baseResponseStatus, Object result) {
        return ResponseEntity
                .status(baseResponseStatus.getStatus())
                .body(BaseResponse.builder()
                        .code(baseResponseStatus.getCode())
                        .message(baseResponseStatus.getMessage())
                        .result(result)
                        .build());
    }
}
