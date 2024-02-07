package blueclub.server.diary.dto.response;

import lombok.Builder;

@Builder
public record GetDayOffDiaryDetailsResponse(
        String worktype
) {
}
