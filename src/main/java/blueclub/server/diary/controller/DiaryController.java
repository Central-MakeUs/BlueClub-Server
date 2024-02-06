package blueclub.server.diary.controller;

import blueclub.server.diary.dto.request.UpdateCaddyDiaryRequest;
import blueclub.server.diary.dto.request.UpdateDayworkerDiaryRequest;
import blueclub.server.diary.dto.request.UpdateDiaryRequest;
import blueclub.server.diary.dto.request.UpdateRiderDiaryRequest;
import blueclub.server.diary.service.DiaryService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.domain.Job;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("")
    public ResponseEntity<BaseResponse> saveDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @NotBlank(message = "직업명은 필수입니다") @RequestParam("job") String jobTitle,
            @Valid @RequestPart("dto") UpdateDiaryRequest updateDiaryRequest,
            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFileList
    ) {
        if (Job.CADDY.getTitle().equals(jobTitle.replace(" ", "")) && updateDiaryRequest instanceof UpdateCaddyDiaryRequest) {
            return BaseResponse.toResponseEntityContainsStatusAndResult(BaseResponseStatus.CREATED, diaryService.saveCaddyDiary(userDetails, (UpdateCaddyDiaryRequest) updateDiaryRequest, multipartFileList));
        } else if (Job.RIDER.getTitle().equals(jobTitle.replace(" ", "")) && updateDiaryRequest instanceof UpdateRiderDiaryRequest) {
            return BaseResponse.toResponseEntityContainsStatusAndResult(BaseResponseStatus.CREATED, diaryService.saveRiderDiary(userDetails, (UpdateRiderDiaryRequest) updateDiaryRequest, multipartFileList));
        } else if (Job.DAYWORKER.getTitle().equals(jobTitle.replace(" ", "")) && updateDiaryRequest instanceof UpdateDayworkerDiaryRequest) {
            return BaseResponse.toResponseEntityContainsStatusAndResult(BaseResponseStatus.CREATED, diaryService.saveDayworkerDiary(userDetails, (UpdateDayworkerDiaryRequest) updateDiaryRequest, multipartFileList));
        } else throw new BaseException(BaseResponseStatus.INVALID_INPUT_DTO);
    }

    @PatchMapping("/{diaryId}")
    public ResponseEntity<BaseResponse> updateDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @NotBlank(message = "직업명은 필수입니다") @RequestParam("job") String jobTitle,
            @PathVariable("diaryId") Long diaryId,
            @Valid @RequestPart("dto") UpdateDiaryRequest updateDiaryRequest,
            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFileList
    ) {
        if (Job.CADDY.getTitle().equals(jobTitle.replace(" ", "")) && updateDiaryRequest instanceof UpdateCaddyDiaryRequest) {
            return BaseResponse.toResponseEntityContainsResult(diaryService.updateCaddyDiary(userDetails, diaryId, (UpdateCaddyDiaryRequest) updateDiaryRequest, multipartFileList));
        } else if (Job.RIDER.getTitle().equals(jobTitle.replace(" ", "")) && updateDiaryRequest instanceof UpdateRiderDiaryRequest) {
            return BaseResponse.toResponseEntityContainsResult(diaryService.updateRiderDiary(userDetails, diaryId, (UpdateRiderDiaryRequest) updateDiaryRequest, multipartFileList));
        } else if (Job.DAYWORKER.getTitle().equals(jobTitle.replace(" ", "")) && updateDiaryRequest instanceof UpdateDayworkerDiaryRequest) {
            return BaseResponse.toResponseEntityContainsResult(diaryService.updateDayworkerDiary(userDetails, diaryId, (UpdateDayworkerDiaryRequest) updateDiaryRequest, multipartFileList));
        } else throw new BaseException(BaseResponseStatus.INVALID_INPUT_DTO);
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<BaseResponse> getDiaryDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @NotBlank(message = "직업명은 필수입니다") @RequestParam("job") String jobTitle,
            @PathVariable("diaryId") Long diaryId
    ) {
        return BaseResponse.toResponseEntityContainsResult(diaryService.getDiaryDetails(userDetails, jobTitle, diaryId));
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<BaseResponse> deleteDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("diaryId") Long diaryId
    ) {
        diaryService.deleteDiary(userDetails, diaryId);
        return BaseResponse.toResponseEntity(BaseResponseStatus.DELETED);
    }

    @GetMapping("/calendar/{yearMonth}")
    public ResponseEntity<BaseResponse> getDailyInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-mm") YearMonth yearMonth
    ) {
        return BaseResponse.toResponseEntityContainsResult(diaryService.getDailyInfo(userDetails, yearMonth));
    }

    @GetMapping("/list/{yearMonth}")
    public ResponseEntity<BaseResponse> getMonthlyList(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-mm") YearMonth yearMonth,
            @RequestParam(value = "diaryId", defaultValue = "-1", required = false) Long id
    ) {
        return BaseResponse.toResponseEntityContainsResult(diaryService.getMonthlyList(userDetails, yearMonth, id));
    }

    @GetMapping("/record/{yearMonth}")
    public ResponseEntity<BaseResponse> getMonthlyRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-mm") YearMonth yearMonth
    ) {
        return BaseResponse.toResponseEntityContainsResult(diaryService.getMonthlyRecord(userDetails, yearMonth));
    }

    @GetMapping("/boast/{diaryId}")
    public ResponseEntity<BaseResponse> getBoastDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("diaryId") Long id
    ) {
        return BaseResponse.toResponseEntityContainsResult(diaryService.getBoastDiary(userDetails, id));
    }
}
