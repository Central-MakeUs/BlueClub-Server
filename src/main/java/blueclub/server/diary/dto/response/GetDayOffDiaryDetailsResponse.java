package blueclub.server.diary.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GetDayOffDiaryDetailsResponse extends GetDiaryIdResponse {
    private String worktype;
}
