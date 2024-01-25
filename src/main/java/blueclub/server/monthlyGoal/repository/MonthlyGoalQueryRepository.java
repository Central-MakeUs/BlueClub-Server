package blueclub.server.monthlyGoal.repository;

import blueclub.server.user.domain.User;

public interface MonthlyGoalQueryRepository {
    Long getRecentMonthlyGoal(User user);
}
