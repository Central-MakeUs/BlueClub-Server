package blueclub.server.diary.repository;

import blueclub.server.diary.domain.Diary;
import blueclub.server.diary.dto.response.GetDailyInfoResponse;
import blueclub.server.user.domain.User;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface DiaryQueryRepository {
    Long getTotalMonthlyIncome(User user, YearMonth yearMonth);
    Integer getTotalWorkingDay(User user, YearMonth yearMonth);
    List<GetDailyInfoResponse> getDailyInfo(User user, YearMonth yearMonth);
    List<Diary> getMonthlyList(User user, YearMonth yearMonth, LocalDate lastDate, Integer pageSize);
    List<Diary> getDiaryById(Long diaryId);
    Integer getStraightWorkingDayLimitMonth(User user, LocalDate workAt);
    Boolean isRenew(User user, LocalDate workAt);
    Integer getStraightWorkingMonth(User user, LocalDate workAt);
}
