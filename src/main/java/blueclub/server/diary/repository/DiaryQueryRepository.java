package blueclub.server.diary.repository;

import blueclub.server.diary.domain.Diary;
import blueclub.server.diary.dto.response.GetDailyInfoResponse;
import blueclub.server.diary.dto.response.MonthlyRecord;
import blueclub.server.user.domain.User;

import java.time.YearMonth;
import java.util.List;

public interface DiaryQueryRepository {
    Long getTotalMonthlyIncome(User user, YearMonth yearMonth);
    Integer getTotalWorkingDay(User user, YearMonth yearMonth);
    List<GetDailyInfoResponse> getDailyInfo(User user, YearMonth yearMonth);
    List<MonthlyRecord> getMonthlyRecord(User user, YearMonth yearMonth);
    List<Diary> getDiaryById(Long diaryId);
}
