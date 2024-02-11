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
public class GetCaddyDiaryDetailsResponse extends GetDiaryIdResponse {
    private String worktype;
    private String memo;
    private List<String> imageUrlList;
    private Long income;
    private Long expenditure;
    private Long saving;

    private Long rounding;
    private Long caddyFee;
    private Long overFee;
    private Boolean topdressing;
}
