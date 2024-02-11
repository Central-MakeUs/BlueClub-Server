package blueclub.server.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetDayworkerDiaryDetailsResponse extends GetDiaryIdResponse {
    private String worktype;
    private String memo;
    private List<String> imageUrlList;
    private Long income;
    private Long expenditure;
    private Long saving;

    private String place;
    private Long dailyWage;
    private String typeOfJob;
    private Double numberOfWork;
}
