package blueclub.server.monthlyGoal.repository;

import blueclub.server.monthlyGoal.domain.MonthlyGoal;
import blueclub.server.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static blueclub.server.monthlyGoal.domain.QMonthlyGoal.monthlyGoal;

@Transactional
@RequiredArgsConstructor
public class MonthlyGoalQueryRepositoryImpl implements MonthlyGoalQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long getRecentMonthlyGoal(User user) {
        List<MonthlyGoal> recentMonthlyGoal = queryFactory
                .selectDistinct(monthlyGoal)
                .from(monthlyGoal)
                .where(monthlyGoal.user.eq(user))
                .limit(1)
                .orderBy(monthlyGoal.yearMonth.desc())
                .fetch();
        // 월 목표 수입 설정 이력이 없는 경우
        if (recentMonthlyGoal.isEmpty())
            return 0L;
        return recentMonthlyGoal.get(0).getTargetIncome();
    }
}
