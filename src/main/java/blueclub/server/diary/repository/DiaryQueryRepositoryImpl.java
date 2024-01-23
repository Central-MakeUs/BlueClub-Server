package blueclub.server.diary.repository;

import blueclub.server.diary.domain.Worktype;
import blueclub.server.diary.dto.response.GetDailyInfoResponse;
import blueclub.server.diary.dto.response.MonthlyRecord;
import blueclub.server.diary.dto.response.QGetDailyInfoResponse;
import blueclub.server.diary.dto.response.QMonthlyRecord;
import blueclub.server.user.domain.Job;
import blueclub.server.user.domain.User;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;

import static blueclub.server.diary.domain.QDiary.diary;

@Transactional
@RequiredArgsConstructor
public class DiaryQueryRepositoryImpl implements DiaryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<GetDailyInfoResponse> getDailyInfo(User user, YearMonth yearMonth) {
        return queryFactory
                .selectDistinct(new QGetDailyInfoResponse(diary.id, diary.workAt, diary.income))
                .from(diary)
                .where(diary.user.eq(user),
                        diary.workAt.year().eq(yearMonth.getYear()),
                        diary.workAt.month().eq(yearMonth.getMonthValue()))
                .orderBy(diary.workAt.asc())
                .fetch();
    }

    @Override
    public Long getTotalMonthlyIncome(User user, YearMonth yearMonth) {
        List<Long> totalMonthlyIncome = queryFactory
                .select(diary.income.sum())
                .from(diary)
                .where(diary.user.eq(user),
                        diary.workAt.year().eq(yearMonth.getYear()),
                        diary.workAt.month().eq(yearMonth.getMonthValue()))
                .fetch();
        return totalMonthlyIncome.get(0) != null ? totalMonthlyIncome.get(0) : 0;
    }

    @Override
    public Integer getTotalWorkingDay(User user, YearMonth yearMonth) {
        List<Long> count = queryFactory
                .select(diary.count())
                .from(diary)
                .where(diary.user.eq(user),
                        diary.workAt.year().eq(yearMonth.getYear()),
                        diary.workAt.month().eq(yearMonth.getMonthValue()),
                        diary.worktype.eq(Worktype.WORKING))
                .fetch();
        return count != null ? count.get(0).intValue() : 0;
    }

    @Override
    public List<MonthlyRecord> getMonthlyRecord(User user, YearMonth yearMonth) {
        return queryFactory
                .selectDistinct(new QMonthlyRecord(diary.id, diary.workAt, diary.worktype.stringValue(), diary.income, getNumberOfCases(user)))
                .from(diary)
                .where(diary.user.eq(user),
                        diary.workAt.year().eq(yearMonth.getYear()),
                        diary.workAt.month().eq(yearMonth.getMonthValue()))
                .limit(4)
                .orderBy(diary.workAt.desc())
                .fetch();
    }

    private NumberPath<Long> getNumberOfCases(User user) {
        if (Job.CADDY.equals(user.getJob()))
            return diary.caddy.rounding;
        else if (Job.RIDER.equals(user.getJob()))
            return diary.rider.numberOfDeliveries;
        return Expressions.numberPath(Long.class, "-1L");
    }
}
