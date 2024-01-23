package blueclub.server.diary.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
public class MonthlyRecord {
    private Long id;
    private LocalDate date;
    private String worktype;
    private Long income;
    private Long numberOfCases;

    @QueryProjection
    public MonthlyRecord(Long id, LocalDate date, String worktype, Long income, Long numberOfCases) {
        this.id = id;
        this.date = date;
        this.worktype = worktype;
        this.income = income;
        this.numberOfCases = numberOfCases;
    }
}
