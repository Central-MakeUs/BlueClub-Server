package blueclub.server.notice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UpdateNoticeRequest (
        @NotBlank(message = "제목은 필수입니다")
        String title,
        @NotBlank(message = "내용은 필수입니다")
        String content
) {
}
