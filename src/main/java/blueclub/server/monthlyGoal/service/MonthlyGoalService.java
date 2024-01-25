package blueclub.server.monthlyGoal.service;

import blueclub.server.diary.service.DiaryService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.monthlyGoal.domain.MonthlyGoal;
import blueclub.server.monthlyGoal.dto.request.UpdateMonthlyGoalRequest;
import blueclub.server.monthlyGoal.dto.response.GetMonthlyGoalResponse;
import blueclub.server.monthlyGoal.repository.MonthlyGoalRepository;
import blueclub.server.user.domain.User;
import blueclub.server.user.service.UserFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MonthlyGoalService {

    private final UserFindService userFindService;
    private final DiaryService diaryService;
    private final MonthlyGoalRepository monthlyGoalRepository;

    public void updateMonthlyGoal(UserDetails userDetails, UpdateMonthlyGoalRequest updateMonthlyGoalRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<MonthlyGoal> monthlyGoal = monthlyGoalRepository.findByUserAndYearMonth(user, updateMonthlyGoalRequest.yearMonth());
        if (monthlyGoal.isPresent()) {
            monthlyGoal.get().updateMonthlyGoal(updateMonthlyGoalRequest.monthlyTargetIncome());
            return;
        }
        monthlyGoalRepository.save(MonthlyGoal.builder()
                .yearMonth(updateMonthlyGoalRequest.yearMonth())
                .targetIncome(updateMonthlyGoalRequest.monthlyTargetIncome())
                .user(user)
                .build());
    }

    @Transactional(readOnly = true)
    public GetMonthlyGoalResponse getMonthlyGoalAndProgress(UserDetails userDetails, YearMonth yearMonth) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<MonthlyGoal> monthlyGoal = monthlyGoalRepository.findByUserAndYearMonth(user, yearMonth);
        if (monthlyGoal.isEmpty()) {
            Long recentMonthlyGoal = monthlyGoalRepository.getRecentMonthlyGoal(user);
            // 첫 사용자에 대한 예외 처리
            if (recentMonthlyGoal == -1) {
                throw new BaseException(BaseResponseStatus.RECENT_MONTHLY_GOAL_NOT_FOUND_ERROR);
            }
            MonthlyGoal newMonthlyGoal = MonthlyGoal.builder()
                    .yearMonth(yearMonth)
                    .targetIncome(recentMonthlyGoal)
                    .user(user)
                    .build();
            monthlyGoalRepository.save(newMonthlyGoal);
            return GetMonthlyGoalResponse.builder()
                    .targetIncome(recentMonthlyGoal)
                    .progress((int) Math.floor((double) diaryService.getTotalMonthlyIncome(user, yearMonth)/recentMonthlyGoal*100))
                    .build();
        }
        return GetMonthlyGoalResponse.builder()
                .targetIncome(monthlyGoal.get().getTargetIncome())
                .progress((int) Math.floor((double) diaryService.getTotalMonthlyIncome(user, yearMonth)/monthlyGoal.get().getTargetIncome()*100))
                .build();
    }
}
