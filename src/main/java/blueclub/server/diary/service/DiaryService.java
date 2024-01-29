package blueclub.server.diary.service;

import blueclub.server.diary.domain.*;
import blueclub.server.diary.dto.request.UpdateCaddyDiaryRequest;
import blueclub.server.diary.dto.request.UpdateDayworkerDiaryRequest;
import blueclub.server.diary.dto.request.UpdateRiderDiaryRequest;
import blueclub.server.diary.dto.response.*;
import blueclub.server.diary.repository.CaddyRepository;
import blueclub.server.diary.repository.DayworkerRepository;
import blueclub.server.diary.repository.DiaryRepository;
import blueclub.server.diary.repository.RiderRepository;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.global.service.S3UploadService;
import blueclub.server.user.domain.Job;
import blueclub.server.user.domain.User;
import blueclub.server.user.service.UserFindService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    public void saveCaddyDiary(UserDetails userDetails, UpdateCaddyDiaryRequest createCaddyDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByKey(createCaddyDiaryRequest.getWorktype()), createCaddyDiaryRequest.getMemo(),
                imageUrlList, createCaddyDiaryRequest.getIncome(), createCaddyDiaryRequest.getExpenditure(),
                createCaddyDiaryRequest.getSaving(), createCaddyDiaryRequest.getDate());
        Diary savedDiary = diaryRepository.save(diary);
        Caddy caddy = Caddy.builder()
                .diary(savedDiary)
                .rounding(createCaddyDiaryRequest.getRounding())
                .caddyFee(createCaddyDiaryRequest.getCaddyFee())
                .overFee(createCaddyDiaryRequest.getOverFee())
                .topdressing(createCaddyDiaryRequest.getTopdressing())
                .build();
        caddyRepository.save(caddy);
        savedDiary.setCaddy(caddy);
    }

    public void saveRiderDiary(UserDetails userDetails, UpdateRiderDiaryRequest createRiderDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByKey(createRiderDiaryRequest.getWorktype()), createRiderDiaryRequest.getMemo(),
                imageUrlList, createRiderDiaryRequest.getIncome(), createRiderDiaryRequest.getExpenditure(),
                createRiderDiaryRequest.getSaving(), createRiderDiaryRequest.getDate());
        Diary savedDiary = diaryRepository.save(diary);
        Rider rider = Rider.builder()
                .diary(savedDiary)
                .numberOfDeliveries(createRiderDiaryRequest.getNumberOfDeliveries())
                .incomeOfDeliveries(createRiderDiaryRequest.getIncomeOfDeliveries())
                .numberOfPromotions(createRiderDiaryRequest.getNumberOfPromotions())
                .incomeOfPromotions(createRiderDiaryRequest.getIncomeOfPromotions())
                .build();
        riderRepository.save(rider);
        savedDiary.setRider(rider);
    }

    public void saveDayworkerDiary(UserDetails userDetails, UpdateDayworkerDiaryRequest createDayworkerDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByKey(createDayworkerDiaryRequest.getWorktype()), createDayworkerDiaryRequest.getMemo(),
                imageUrlList, createDayworkerDiaryRequest.getIncome(), createDayworkerDiaryRequest.getExpenditure(),
                createDayworkerDiaryRequest.getSaving(), createDayworkerDiaryRequest.getDate());
        Diary savedDiary = diaryRepository.save(diary);
        Dayworker dayworker = Dayworker.builder()
                .diary(savedDiary)
                .place(createDayworkerDiaryRequest.getPlace())
                .dailyWage(createDayworkerDiaryRequest.getDailyWage())
                .typeOfJob(createDayworkerDiaryRequest.getTypeOfJob())
                .numberOfWork(createDayworkerDiaryRequest.getNumberOfWork())
                .build();
        dayworkerRepository.save(dayworker);
        savedDiary.setDayworker(dayworker);
    }

    public void updateCaddyDiary(UserDetails userDetails, Long diaryId, UpdateCaddyDiaryRequest updateCaddyDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<Diary> diary = diaryRepository.findById(diaryId);
        if (diary.isEmpty()) {
            throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR);
        }
        Caddy caddy = diary.get().getCaddy();
        List<String> updateImageUrlList = Stream.concat(
                        CollectionUtils.emptyIfNull(updateCaddyDiaryRequest.getImageUrlList()).stream(),
                        CollectionUtils.emptyIfNull(uploadDiaryImage(multipartFileList)).stream())
                .toList();
        diary.get().update(
                Worktype.findByKey(updateCaddyDiaryRequest.getWorktype()),
                updateCaddyDiaryRequest.getMemo(),
                updateImageUrlList,
                updateCaddyDiaryRequest.getIncome(),
                updateCaddyDiaryRequest.getExpenditure(),
                updateCaddyDiaryRequest.getSaving());
        caddy.update(
                updateCaddyDiaryRequest.getRounding(),
                updateCaddyDiaryRequest.getCaddyFee(),
                updateCaddyDiaryRequest.getOverFee(),
                updateCaddyDiaryRequest.getTopdressing());
    }

    public void updateRiderDiary(UserDetails userDetails, Long diaryId, UpdateRiderDiaryRequest updateRiderDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<Diary> diary = diaryRepository.findById(diaryId);
        if (diary.isEmpty()) {
            throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR);
        }
        Rider rider = diary.get().getRider();
        List<String> updateImageUrlList = Stream.concat(
                        CollectionUtils.emptyIfNull(updateRiderDiaryRequest.getImageUrlList()).stream(),
                        CollectionUtils.emptyIfNull(uploadDiaryImage(multipartFileList)).stream())
                .toList();
        diary.get().update(
                Worktype.findByKey(updateRiderDiaryRequest.getWorktype()),
                updateRiderDiaryRequest.getMemo(),
                updateImageUrlList,
                updateRiderDiaryRequest.getIncome(),
                updateRiderDiaryRequest.getExpenditure(),
                updateRiderDiaryRequest.getSaving());
        rider.update(
                updateRiderDiaryRequest.getNumberOfDeliveries(),
                updateRiderDiaryRequest.getIncomeOfDeliveries(),
                updateRiderDiaryRequest.getNumberOfPromotions(),
                updateRiderDiaryRequest.getIncomeOfPromotions());
    }

    public void updateDayworkerDiary(UserDetails userDetails, Long diaryId, UpdateDayworkerDiaryRequest updateDayworkerDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<Diary> diary = diaryRepository.findById(diaryId);
        if (diary.isEmpty()) {
            throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR);
        }
        Dayworker dayworker = diary.get().getDayworker();
        List<String> updateImageUrlList = Stream.concat(
                CollectionUtils.emptyIfNull(updateDayworkerDiaryRequest.getImageUrlList()).stream(),
                CollectionUtils.emptyIfNull(uploadDiaryImage(multipartFileList)).stream())
                .toList();
        diary.get().update(
                Worktype.findByKey(updateDayworkerDiaryRequest.getWorktype()),
                updateDayworkerDiaryRequest.getMemo(),
                updateImageUrlList,
                updateDayworkerDiaryRequest.getIncome(),
                updateDayworkerDiaryRequest.getExpenditure(),
                updateDayworkerDiaryRequest.getSaving());
        dayworker.update(
                updateDayworkerDiaryRequest.getPlace(),
                updateDayworkerDiaryRequest.getDailyWage(),
                updateDayworkerDiaryRequest.getTypeOfJob(),
                updateDayworkerDiaryRequest.getNumberOfWork());
    }

    @Transactional(readOnly = true)
    public Object getDiaryDetails(UserDetails userDetails, String jobTitle, Long diaryId) {
        User user = userFindService.findByUserDetails(userDetails);
        List<Diary> diary = diaryRepository.getDiaryById(diaryId);
        if (diary.isEmpty()) {
            throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR);
        }
        if (Job.CADDY.getTitle().equals(jobTitle)) {
            return GetCaddyDiaryDetailsResponse.builder()
                    .worktype(diary.get(0).getWorktype().getKey())
                    .memo(diary.get(0).getMemo())
                    .imageUrlList(diary.get(0).getImage())
                    .income(diary.get(0).getIncome())
                    .expenditure(diary.get(0).getExpenditure())
                    .saving(diary.get(0).getSaving())
                    .rounding(diary.get(0).getCaddy().getRounding())
                    .caddyFee(diary.get(0).getCaddy().getCaddyFee())
                    .overFee(diary.get(0).getCaddy().getOverFee())
                    .topdressing(diary.get(0).getCaddy().getTopdressing())
                    .build();
        } else if (Job.RIDER.getTitle().equals(jobTitle)) {
            return GetRiderDiaryDetailsResponse.builder()
                    .worktype(diary.get(0).getWorktype().getKey())
                    .memo(diary.get(0).getMemo())
                    .imageUrlList(diary.get(0).getImage())
                    .income(diary.get(0).getIncome())
                    .expenditure(diary.get(0).getExpenditure())
                    .saving(diary.get(0).getSaving())
                    .incomeOfDeliveries(diary.get(0).getRider().getIncomeOfDeliveries())
                    .numberOfDeliveries(diary.get(0).getRider().getNumberOfDeliveries())
                    .incomeOfPromotions(diary.get(0).getRider().getIncomeOfPromotions())
                    .numberOfPromotions(diary.get(0).getRider().getNumberOfPromotions())
                    .build();
        } else if (Job.DAYWORKER.getTitle().equals(jobTitle)) {
            return GetDayworkerDiaryDetailsResponse.builder()
                    .worktype(diary.get(0).getWorktype().getKey())
                    .memo(diary.get(0).getMemo())
                    .imageUrlList(diary.get(0).getImage())
                    .income(diary.get(0).getIncome())
                    .expenditure(diary.get(0).getExpenditure())
                    .saving(diary.get(0).getSaving())
                    .place(diary.get(0).getDayworker().getPlace())
                    .dailyWage(diary.get(0).getDayworker().getDailyWage())
                    .typeOfJob(diary.get(0).getDayworker().getTypeOfJob())
                    .numberOfWork(diary.get(0).getDayworker().getNumberOfWork())
                    .build();
        }
        throw new BaseException(BaseResponseStatus.JOB_NOT_FOUND_ERROR);
    }

    public void deleteDiary(UserDetails userDetails, Long diaryId) {
        diaryRepository.deleteById(diaryId);
    }

    @Transactional(readOnly = true)
    public List<GetDailyInfoResponse> getDailyInfo(UserDetails userDetails, YearMonth yearMonth) {
        User user = userFindService.findByUserDetails(userDetails);
        return diaryRepository.getDailyInfo(user, yearMonth);
    }

    @Transactional(readOnly = true)
    public GetMonthlyRecordResponse getMonthlyRecord(UserDetails userDetails, YearMonth yearMonth) {
        User user = userFindService.findByUserDetails(userDetails);
        return GetMonthlyRecordResponse.builder()
                .totalWorkingDay(diaryRepository.getTotalWorkingDay(user, yearMonth))
                .monthlyRecord(diaryRepository.getMonthlyRecord(user, yearMonth))
                .build();
    }

    @Transactional(readOnly = true)
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
        if (multipartFileList == null)
            return imageUrlList;
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
