package blueclub.server.monthlyGoal.controller;

import blueclub.server.global.annotation.LocalDatePattern;
import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.monthlyGoal.dto.request.UpdateMonthlyGoalRequest;
import blueclub.server.monthlyGoal.service.MonthlyGoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.YearMonth;

@PreAuthorize("hasRole('USER')")
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/monthly_goal")
public class MonthlyGoalController {

    private final MonthlyGoalService monthlyGoalService;

    @PostMapping("")
    public ResponseEntity<BaseResponse> updateMonthlyGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateMonthlyGoalRequest updateMonthlyGoalRequest
    ) {
        monthlyGoalService.updateMonthlyGoal(userDetails, updateMonthlyGoalRequest);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @GetMapping("/{yearMonth}")
    public ResponseEntity<BaseResponse> getMonthlyGoalAndProgress(
            @AuthenticationPrincipal UserDetails userDetails,
            @LocalDatePattern(pattern = "yyyy-M")
            @PathVariable("yearMonth")
            String yearMonth
    ) {
        return BaseResponse.toResponseEntityContainsResult(monthlyGoalService.getMonthlyGoalAndProgress(userDetails, YearMonth.parse(yearMonth)));
    }
}
