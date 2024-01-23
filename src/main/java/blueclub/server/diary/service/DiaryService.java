package blueclub.server.diary.service;

import blueclub.server.diary.domain.*;
import blueclub.server.diary.dto.request.CreateCaddyDiaryRequest;
import blueclub.server.diary.dto.request.CreateDayworkerDiaryRequest;
import blueclub.server.diary.dto.request.CreateRiderDiaryRequest;
import blueclub.server.diary.dto.response.GetDailyInfoResponse;
import blueclub.server.diary.dto.response.GetMonthlyRecordResponse;
import blueclub.server.diary.repository.CaddyRepository;
import blueclub.server.diary.repository.DayworkerRepository;
import blueclub.server.diary.repository.DiaryRepository;
import blueclub.server.diary.repository.RiderRepository;
import blueclub.server.global.service.S3UploadService;
import blueclub.server.user.domain.User;
import blueclub.server.user.service.UserFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DiaryService {

    private final UserFindService userFindService;
    private final S3UploadService s3UploadService;
    private final DiaryRepository diaryRepository;
    private final CaddyRepository caddyRepository;
    private final RiderRepository riderRepository;
    private final DayworkerRepository dayworkerRepository;

    public void saveCaddyDiary(UserDetails userDetails, CreateCaddyDiaryRequest createCaddyDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByKey(createCaddyDiaryRequest.worktype()), createCaddyDiaryRequest.memo(),
                imageUrlList, createCaddyDiaryRequest.income(), createCaddyDiaryRequest.expenditure(),
                createCaddyDiaryRequest.saving(), createCaddyDiaryRequest.date());
        Diary savedDiary = diaryRepository.save(diary);
        Caddy caddy = Caddy.builder()
                .diary(savedDiary)
                .rounding(createCaddyDiaryRequest.rounding())
                .caddyFee(createCaddyDiaryRequest.caddyFee())
                .overFee(createCaddyDiaryRequest.overFee())
                .topdressing(createCaddyDiaryRequest.topdressing())
                .build();
        caddyRepository.save(caddy);
        savedDiary.setCaddy(caddy);
    }

    public void saveRiderDiary(UserDetails userDetails, CreateRiderDiaryRequest createRiderDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByKey(createRiderDiaryRequest.worktype()), createRiderDiaryRequest.memo(),
                imageUrlList, createRiderDiaryRequest.income(), createRiderDiaryRequest.expenditure(),
                createRiderDiaryRequest.saving(), createRiderDiaryRequest.date());
        Diary savedDiary = diaryRepository.save(diary);
        Rider rider = Rider.builder()
                .diary(savedDiary)
                .numberOfDeliveries(createRiderDiaryRequest.numberOfDeliveries())
                .incomeOfDeliveries(createRiderDiaryRequest.incomeOfDeliveries())
                .numberOfPromotions(createRiderDiaryRequest.numberOfPromotions())
                .incomeOfPromotions(createRiderDiaryRequest.incomeOfPromotions())
                .build();
        riderRepository.save(rider);
        savedDiary.setRider(rider);
    }

    public void saveDayworkerDiary(UserDetails userDetails, CreateDayworkerDiaryRequest createDayworkerDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByKey(createDayworkerDiaryRequest.worktype()), createDayworkerDiaryRequest.memo(),
                imageUrlList, createDayworkerDiaryRequest.income(), createDayworkerDiaryRequest.expenditure(),
                createDayworkerDiaryRequest.saving(), createDayworkerDiaryRequest.date());
        Diary savedDiary = diaryRepository.save(diary);
        Dayworker dayworker = Dayworker.builder()
                .diary(savedDiary)
                .place(createDayworkerDiaryRequest.place())
                .dailyWage(createDayworkerDiaryRequest.dailyWage())
                .typeOfJob(createDayworkerDiaryRequest.typeOfJob())
                .numberOfWork(createDayworkerDiaryRequest.numberOfWork())
                .build();
        dayworkerRepository.save(dayworker);
        savedDiary.setDayworker(dayworker);
    }

    public List<GetDailyInfoResponse> getDailyInfo(UserDetails userDetails, YearMonth yearMonth) {
        User user = userFindService.findByUserDetails(userDetails);
        return diaryRepository.getDailyInfo(user, yearMonth);
    }

    public GetMonthlyRecordResponse getMonthlyRecord(UserDetails userDetails, YearMonth yearMonth) {
        User user = userFindService.findByUserDetails(userDetails);
        return GetMonthlyRecordResponse.builder()
                .totalWorkingDay(diaryRepository.getTotalWorkingDay(user, yearMonth))
                .monthlyRecord(diaryRepository.getMonthlyRecord(user, yearMonth))
                .build();
    }

    public Long getTotalMonthlyIncome(User user, YearMonth yearMonth) {
        return diaryRepository.getTotalMonthlyIncome(user, yearMonth);
    }

    private Diary saveDiary(User user, Worktype worktype, String memo, List<String> imageUrlList, Long income, Long expenditure, Long saving, LocalDate workAt) {
        return Diary.builder()
                .worktype(worktype)
                .memo(memo)
                .image(imageUrlList)
                .income(income)
                .expenditure(expenditure)
                .saving(saving)
                .workAt(workAt)
                .user(user)
                .build();
    }

    private List<String> uploadDiaryImage(List<MultipartFile> multipartFileList) {
        List<String> imageUrlList = new ArrayList<>();
        for (MultipartFile multipartFile: multipartFileList) {
            try { // 파일 업로드
                String fileName = s3UploadService.upload(multipartFile, "diary"); // S3 버킷의 images 디렉토리 안에 저장됨
                imageUrlList.add(fileName);
            } catch (IOException e) {
                throw new RuntimeException();
            }
        }
        return imageUrlList;
    }
}
