package blueclub.server.monthlyGoal.service;

import blueclub.server.diary.repository.DiaryRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class MonthlyGoalService {

    private final UserFindService userFindService;
    private final DiaryRepository diaryRepository;
    private final MonthlyGoalRepository monthlyGoalRepository;

    public void updateMonthlyGoal(UserDetails userDetails, UpdateMonthlyGoalRequest updateMonthlyGoalRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<MonthlyGoal> monthlyGoal = monthlyGoalRepository.findByUserAndYearMonth(user, YearMonth.parse(updateMonthlyGoalRequest.yearMonth(), DateTimeFormatter.ofPattern("yyyy-M")));
        if (monthlyGoal.isPresent()) {
            monthlyGoal.get().updateMonthlyGoal(updateMonthlyGoalRequest.monthlyTargetIncome());
            return;
        }
        saveMonthlyGoal(user, YearMonth.parse(updateMonthlyGoalRequest.yearMonth(), DateTimeFormatter.ofPattern("yyyy-M")), updateMonthlyGoalRequest.monthlyTargetIncome());
    }

    public GetMonthlyGoalResponse getMonthlyGoalAndProgress(UserDetails userDetails, YearMonth yearMonth) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<MonthlyGoal> monthlyGoal = monthlyGoalRepository.findByUserAndYearMonth(user, yearMonth);
        Long totalIncome = diaryRepository.getTotalMonthlyIncome(user, yearMonth);

        if (monthlyGoal.isEmpty()) {
            Long recentMonthlyGoal = monthlyGoalRepository.getRecentMonthlyGoal(user, yearMonth);
            // 첫 사용자에 대한 예외 처리
            if (recentMonthlyGoal == -1L) {
                return GetMonthlyGoalResponse.builder()
                        .targetIncome(0L)
                        .totalIncome(totalIncome)
                        .progress(0)
                        .build();
            }

            updateMonthlyGoal(userDetails, UpdateMonthlyGoalRequest.builder()
                    .yearMonth(yearMonth.toString())
                    .monthlyTargetIncome(recentMonthlyGoal)
                    .build());

            return GetMonthlyGoalResponse.builder()
                    .targetIncome(recentMonthlyGoal)
                    .totalIncome(totalIncome)
                    .progress((int) Math.min(Math.floor((double) totalIncome/recentMonthlyGoal*100), 100))
                    .build();
        }
        return GetMonthlyGoalResponse.builder()
                .targetIncome(monthlyGoal.get().getTargetIncome())
                .totalIncome(totalIncome)
                .progress((int) Math.min(Math.floor((double) totalIncome/monthlyGoal.get().getTargetIncome()*100), 100))
                .build();
    }

    public void saveMonthlyGoal(User user, YearMonth yearMonth, Long monthlyTargetIncome) {
        monthlyGoalRepository.save(MonthlyGoal.builder()
                .yearMonth(yearMonth)
                .targetIncome(monthlyTargetIncome)
                .user(user)
                .build());
    }
}
