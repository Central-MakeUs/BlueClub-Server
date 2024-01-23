package blueclub.server.diary.controller;

import blueclub.server.diary.dto.request.CreateCaddyDiaryRequest;
import blueclub.server.diary.dto.request.CreateDayworkerDiaryRequest;
import blueclub.server.diary.dto.request.CreateRiderDiaryRequest;
import blueclub.server.diary.service.DiaryService;
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

    @PostMapping("/caddy")
    public ResponseEntity<BaseResponse> saveCaddyDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("dto") CreateCaddyDiaryRequest createCaddyDiaryRequest,
            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFileList) {
        diaryService.saveCaddyDiary(userDetails, createCaddyDiaryRequest, multipartFileList);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.CREATED);
    }

    @PostMapping("/rider")
    public ResponseEntity<BaseResponse> saveRiderDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("dto") CreateRiderDiaryRequest createRiderDiaryRequest,
            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFileList) {
        diaryService.saveRiderDiary(userDetails, createRiderDiaryRequest, multipartFileList);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.CREATED);
    }

    @PostMapping("/dayworker")
    public ResponseEntity<BaseResponse> saveDayworkerDiary(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestPart("dto") CreateDayworkerDiaryRequest createDayworkerDiaryRequest,
            @RequestPart(value = "image", required = false) List<MultipartFile> multipartFileList) {
        diaryService.saveDayworkerDiary(userDetails, createDayworkerDiaryRequest, multipartFileList);
        return BaseResponse.toResponseEntityContainsStatus(BaseResponseStatus.CREATED);
    }

    @GetMapping("/info/{yearMonth}")
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
