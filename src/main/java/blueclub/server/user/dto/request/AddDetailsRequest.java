package blueclub.server.user.dto.request;

import lombok.Builder;

@Builder
public record AddDetailsRequest (
        String nickname,
        String jobTitle,
        Integer jobStart,
        Boolean tosAgree
) {
}
