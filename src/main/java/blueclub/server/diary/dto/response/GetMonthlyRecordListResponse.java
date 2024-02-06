package blueclub.server.diary.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record GetMonthlyRecordListResponse(
        Integer totalDay,
        List<MonthlyRecord> monthlyRecord
) {
}
