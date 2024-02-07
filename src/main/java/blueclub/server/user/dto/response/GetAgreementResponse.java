package blueclub.server.user.dto.response;

import lombok.Builder;

@Builder
public record GetAgreementResponse(
        Boolean tosAgree,
        Boolean pushAgree
) {
}
