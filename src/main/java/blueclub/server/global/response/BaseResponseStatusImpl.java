package blueclub.server.global.response;

import org.springframework.http.HttpStatus;

public interface BaseResponseStatusImpl {
    HttpStatus getStatus();
    String getCode();
    String getMessage();
}
