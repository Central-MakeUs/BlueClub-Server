package blueclub.server.global.response;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BaseException extends RuntimeException {
    BaseResponseStatus errorStatus;
}
