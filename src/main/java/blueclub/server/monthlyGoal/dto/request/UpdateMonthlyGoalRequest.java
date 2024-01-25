package blueclub.server.monthlyGoal.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Builder;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.YearMonth;

@Builder
public record UpdateMonthlyGoalRequest (
        @DateTimeFormat(pattern = "yyyy-mm")
        YearMonth yearMonth,
        @Min(value = 100000, message = "월 수입 목표는 10만원 이상으로 입력해주세요")
        @Max(value = 99990000, message = "월 수입 목표는 9999만원 이하로 입력해주세요")
        Long monthlyTargetIncome
) {
}
