package blueclub.server.diary.dto.response;

import lombok.Builder;

@Builder
public record GetMonthlyRecordResponse(
        Integer totalDay,
        Integer straightDay,
        Boolean isRenew,
        Integer straightMonth,
        Long targetIncome,
        Long totalIncome,
        Integer progress
) {
}
