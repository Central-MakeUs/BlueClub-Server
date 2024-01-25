package blueclub.server.diary.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
public class GetDailyInfoResponse {
    private Long id;
    private LocalDate date;
    private Long income;

    @QueryProjection
    public GetDailyInfoResponse(Long id, LocalDate date, Long income) {
        this.id = id;
        this.date = date;
        this.income = income;
    }
}
