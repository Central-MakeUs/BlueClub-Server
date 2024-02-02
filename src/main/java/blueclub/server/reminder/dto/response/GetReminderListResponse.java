package blueclub.server.reminder.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record GetReminderListResponse (
        Long id,
        String title,
        String content,
        LocalDateTime createAt
) {
    @QueryProjection
    public GetReminderListResponse {
    }
}
