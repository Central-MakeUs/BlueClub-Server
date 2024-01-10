package blueclub.server.user.dto.request;

public record AddDetailsRequest (
        String nickname,
        String jobTitle,
        Integer jobStart,
        Boolean tosAgree
) {
}
