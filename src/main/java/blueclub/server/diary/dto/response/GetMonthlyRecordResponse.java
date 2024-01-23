package blueclub.server.diary.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GetMonthlyRecordResponse(
        Integer totalWorkingDay,
        List<MonthlyRecord> monthlyRecord
) {
}
