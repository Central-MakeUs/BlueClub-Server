package blueclub.server.notice.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;

public record GetNoticeListResponse (
        Long id,
        String title,
        String content,
        LocalDateTime createAt
) {
    @QueryProjection
    public GetNoticeListResponse {
    }
}
