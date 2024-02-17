package blueclub.server.monthlyGoal.repository;

import blueclub.server.user.domain.User;

import java.time.YearMonth;

public interface MonthlyGoalQueryRepository {
    Long getRecentMonthlyGoal(User user, YearMonth yearMonth);
}
