package blueclub.server.monthlyGoal.repository;

import blueclub.server.monthlyGoal.domain.MonthlyGoal;
import blueclub.server.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.YearMonth;
import java.util.Optional;

public interface MonthlyGoalRepository extends JpaRepository<MonthlyGoal, Long>, MonthlyGoalQueryRepository {

    Optional<MonthlyGoal> findByUserAndYearMonth(User user, YearMonth yearMonth);
}
