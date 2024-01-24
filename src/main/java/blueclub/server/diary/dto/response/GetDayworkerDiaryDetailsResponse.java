package blueclub.server.diary.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GetDayworkerDiaryDetailsResponse(
        String worktype,
        String memo,
        List<String> imageUrlList,
        Long income,
        Long expenditure,
        Long saving,

        String place,
        Long dailyWage,
        String typeOfJob,
        Double numberOfWork
) {
}
