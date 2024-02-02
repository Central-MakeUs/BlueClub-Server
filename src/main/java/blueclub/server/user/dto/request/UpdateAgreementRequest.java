package blueclub.server.user.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateAgreementRequest (
        @NotNull(message = "선택약관 동의여부를 입력해주세요")
        Boolean tosAgree,
        @NotNull(message = "푸시알림 동의여부를 입력해주세요")
        Boolean pushAgree
) {
}
