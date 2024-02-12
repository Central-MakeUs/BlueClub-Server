package blueclub.server.diary.repository;

import blueclub.server.diary.domain.Diary;
import blueclub.server.user.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static blueclub.server.diary.domain.QDiary.diary;

@Transactional
@RequiredArgsConstructor
public class DiaryQueryRepositoryImpl implements DiaryQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Long getTotalMonthlyIncome(User user, YearMonth yearMonth) {
        List<Long> totalMonthlyIncome = queryFactory
                .select(diary.income.sum())
                .from(diary)
                .where(diary.user.eq(user),
                        diary.workAt.year().eq(yearMonth.getYear()),
                        diary.workAt.month().eq(yearMonth.getMonthValue()),
                        diary.job.eq(user.getJob()))
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
                        diary.job.eq(user.getJob()))
                .fetch();
        return count != null ? count.get(0).intValue() : 0;
    }

    @Override
    public List<Diary> getMonthlyList(User user, YearMonth yearMonth) {
        return queryFactory
                .selectDistinct(diary)
                .from(diary)
                .where(diary.user.eq(user),
                        diary.workAt.year().eq(yearMonth.getYear()),
                        diary.workAt.month().eq(yearMonth.getMonthValue()),
                        diary.job.eq(user.getJob()))
                .orderBy(diary.workAt.desc())
                .fetch();
    }

    @Override
    public List<Diary> getDiaryById(User user, Long diaryId) {
        return queryFactory
                .selectDistinct(diary)
                .from(diary)
                .leftJoin(diary.image)
                .fetchJoin()
                .where(diary.user.eq(user),
                        diary.id.eq(diaryId),
                        diary.job.eq(user.getJob()))
                .fetch();
    }

    @Override
    public List<Diary> getDiaryByDate(User user, LocalDate date) {
        return queryFactory
                .selectDistinct(diary)
                .from(diary)
                .leftJoin(diary.image)
                .fetchJoin()
                .where(diary.user.eq(user),
                        diary.workAt.eq(date),
                        diary.job.eq(user.getJob()))
                .fetch();
    }

    @Override
    public Integer getStraightWorkingDayLimitMonth(User user, LocalDate workAt) {
        List<LocalDate> workAtList = queryFactory
                .selectDistinct(diary.workAt)
                .from(diary)
                .where(diary.user.eq(user),
                        diary.workAt.year().eq(workAt.getYear()),
                        diary.workAt.month().eq(workAt.getMonthValue()),
                        diary.job.eq(user.getJob()))
                .orderBy(diary.workAt.desc())
                .fetch();

        LocalDate targetDate = workAt;
        Integer straightWorkingDay = 0;
        for (LocalDate compareDate: workAtList) {
            if (!compareDate.isEqual(targetDate))
                break;

            targetDate = targetDate.minusDays(1);
            straightWorkingDay++;
        }
        return straightWorkingDay;
    }

    @Override
    public Boolean isRenew(User user, LocalDate workAt) {
        List<LocalDate> workAtList = queryFactory
                .selectDistinct(diary.workAt)
                .from(diary)
                .where(diary.user.eq(user),
                        diary.job.eq(user.getJob()))
                .orderBy(diary.workAt.desc())
                .fetch();

        LocalDate targetDate = workAt;
        Integer maxStraightWorkingDay = 0;
        Integer straightWorkingDay = 0;
        Integer currentStraightWorkingDay = 0;
        Boolean isCurrent = true;
        for (LocalDate compareDate: workAtList) {
            if (!compareDate.isEqual(targetDate)) {
                if (isCurrent) {
                    currentStraightWorkingDay = straightWorkingDay;
                    isCurrent = false;
                }
                if (maxStraightWorkingDay < straightWorkingDay)
                    maxStraightWorkingDay = straightWorkingDay;
                straightWorkingDay = 1;
                targetDate = compareDate.minusDays(1);
            }

            targetDate = targetDate.minusDays(1);
            straightWorkingDay++;
        }
        if (isCurrent)
            currentStraightWorkingDay = straightWorkingDay;
        if (maxStraightWorkingDay < straightWorkingDay)
            maxStraightWorkingDay = straightWorkingDay;

        return maxStraightWorkingDay.equals(currentStraightWorkingDay);
    }

    @Override
    public Integer getStraightWorkingMonth(User user, LocalDate workAt) {
        List<LocalDate> workAtList = queryFactory
                .selectDistinct(diary.workAt)
                .from(diary)
                .where(diary.user.eq(user),
                        diary.job.eq(user.getJob()))
                .orderBy(diary.workAt.desc())
                .fetch();

        LocalDate targetDate = workAt;
        Integer straightWorkingMonth = 0;
        for (LocalDate compareDate: workAtList) {
            if (!compareDate.isEqual(targetDate))
                break;

            if (targetDate.equals(targetDate.with(TemporalAdjusters.firstDayOfMonth())))
                straightWorkingMonth++;
            targetDate = targetDate.minusDays(1);
        }
        if (!workAt.equals(targetDate.with(TemporalAdjusters.lastDayOfMonth())))
            straightWorkingMonth--;
        if (straightWorkingMonth.equals(-1))
            return 0;
        return straightWorkingMonth;
    }

    /*
    private BooleanExpression getWhereQueryByDiaryJob(Job currentJob) {
        return switch (currentJob) {
            case CADDY -> diary.caddy.isNotNull();
            case RIDER -> diary.rider.isNotNull();
            case DAYWORKER -> diary.dayworker.isNotNull();
        };
    }

    public NumberPath<Long> getNumberOfCases(User user) {
        if (diary.worktype.toString().equals(Worktype.DAY_OFF.toString()))
            return Expressions.numberPath(Long.class, "-1");

        if (Job.CADDY.equals(user.getJob()))
            return diary.caddy.rounding;
        else if (Job.RIDER.equals(user.getJob()))
            return diary.rider.numberOfDeliveries;
        return Expressions.numberPath(Long.class, "-1");
    }

     */
}
