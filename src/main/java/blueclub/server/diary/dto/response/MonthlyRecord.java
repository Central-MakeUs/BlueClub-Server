package blueclub.server.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRecord {
    private Long id;
    private LocalDate date;
    private String worktype;
    private Long income;
    private Long cases;

    /*

    @QueryProjection
    public MonthlyRecord(Long id, LocalDate date, String worktype, Long income, Long cases) {
        this.id = id;
        this.date = date;
        this.worktype = Worktype.valueOf(worktype).getKey();
        this.income = income;
        this.cases = cases;
    }

     */
}
