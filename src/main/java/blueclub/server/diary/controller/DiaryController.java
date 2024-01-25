package blueclub.server.diary.controller;

import blueclub.server.diary.dto.request.UpdateCaddyDiaryRequest;
import blueclub.server.diary.dto.request.UpdateDayworkerDiaryRequest;
import blueclub.server.diary.dto.request.UpdateDiaryRequest;
import blueclub.server.diary.dto.request.UpdateRiderDiaryRequest;
import blueclub.server.diary.service.DiaryService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping("")
    public ResponseEntity<BaseResponse> saveDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("dto") UpdateDiaryRequest updateDiaryRequest,
            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFileList) {
        if (updateDiaryRequest instanceof UpdateCaddyDiaryRequest) {
            diaryService.saveCaddyDiary(userDetails, (UpdateCaddyDiaryRequest) updateDiaryRequest, multipartFileList);
        } else if (updateDiaryRequest instanceof UpdateRiderDiaryRequest) {
            diaryService.saveRiderDiary(userDetails, (UpdateRiderDiaryRequest) updateDiaryRequest, multipartFileList);
        } else if (updateDiaryRequest instanceof UpdateDayworkerDiaryRequest) {
            diaryService.saveDayworkerDiary(userDetails, (UpdateDayworkerDiaryRequest) updateDiaryRequest, multipartFileList);
        } else throw new BaseException(BaseResponseStatus.INVALID_INPUT_DTO);

        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.CREATED);
    }

    @PatchMapping("/{diaryId}")
    public ResponseEntity<BaseResponse> updateDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("diaryId") Long diaryId,
            @Valid @RequestPart("dto") UpdateDiaryRequest updateDiaryRequest,
            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFileList) {
        if (updateDiaryRequest instanceof UpdateCaddyDiaryRequest) {
            diaryService.updateCaddyDiary(userDetails, diaryId, (UpdateCaddyDiaryRequest) updateDiaryRequest, multipartFileList);
        } else if (updateDiaryRequest instanceof UpdateRiderDiaryRequest) {
            diaryService.updateRiderDiary(userDetails, diaryId, (UpdateRiderDiaryRequest) updateDiaryRequest, multipartFileList);
        } else if (updateDiaryRequest instanceof UpdateDayworkerDiaryRequest) {
            diaryService.updateDayworkerDiary(userDetails, diaryId, (UpdateDayworkerDiaryRequest) updateDiaryRequest, multipartFileList);
        } else throw new BaseException(BaseResponseStatus.INVALID_INPUT_DTO);

        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.SUCCESS);
    }

    @GetMapping("/{diaryId}")
    public ResponseEntity<BaseResponse> getDiaryDetails(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("diaryId") Long diaryId) {
        return BaseResponse.toResponseEntityContainsResult(diaryService.getDiaryDetails(userDetails, diaryId));
    }

    @DeleteMapping("/{diaryId}")
    public ResponseEntity<BaseResponse> deleteDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("diaryId") Long diaryId) {
        diaryService.deleteDiary(userDetails, diaryId);
        return BaseResponse.toResponseEntity(BaseResponseStatus.DELETED);
    }

    @GetMapping("/calendar/{yearMonth}")
    public ResponseEntity<BaseResponse> getDailyInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-mm") YearMonth yearMonth) {
        return BaseResponse.toResponseEntityContainsResult(diaryService.getDailyInfo(userDetails, yearMonth));
    }

    @GetMapping("/record/{yearMonth}")
    public ResponseEntity<BaseResponse> getMonthlyRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("yearMonth") @DateTimeFormat(pattern = "yyyy-mm") YearMonth yearMonth) {
        return BaseResponse.toResponseEntityContainsResult(diaryService.getMonthlyRecord(userDetails, yearMonth));
    }
}
