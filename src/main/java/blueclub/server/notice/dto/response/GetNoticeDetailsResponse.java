package blueclub.server.notice.dto.response;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetNoticeDetailsResponse (
        String title,
        String content,
        LocalDateTime createAt
) {
}
