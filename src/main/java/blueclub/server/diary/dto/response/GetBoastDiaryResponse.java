package blueclub.server.diary.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record GetBoastDiaryResponse(
        String job,
        LocalDate workAt,
        String rank,
        Long income,
        Long cases
) {
    @QueryProjection
    public GetBoastDiaryResponse {
    }
}
