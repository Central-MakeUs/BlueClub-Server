package blueclub.server.monthlyGoal.dto.response;

import lombok.Builder;

@Builder
public record GetMonthlyGoalResponse (
        Long targetIncome,
        Long totalIncome,
        Integer progress
) {
}
