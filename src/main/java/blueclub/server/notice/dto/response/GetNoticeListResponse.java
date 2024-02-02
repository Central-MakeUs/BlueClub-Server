package blueclub.server.notice.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
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
