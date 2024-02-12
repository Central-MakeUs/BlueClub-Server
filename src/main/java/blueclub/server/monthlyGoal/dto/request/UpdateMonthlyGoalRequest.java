package blueclub.server.monthlyGoal.dto.request;

import blueclub.server.global.annotation.LocalDatePattern;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record UpdateMonthlyGoalRequest (
        @LocalDatePattern(pattern = "yyyy-MM")
        String yearMonth,
        @Min(value = 100000, message = "월 수입 목표는 10만원 이상으로 입력해주세요")
        @Max(value = 99990000, message = "월 수입 목표는 9999만원 이하로 입력해주세요")
        Long monthlyTargetIncome
) {
}
