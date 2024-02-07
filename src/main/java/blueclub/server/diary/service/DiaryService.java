package blueclub.server.diary.service;

import blueclub.server.diary.domain.*;
import blueclub.server.diary.dto.request.UpdateBaseDiaryRequest;
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
import blueclub.server.monthlyGoal.domain.MonthlyGoal;
import blueclub.server.monthlyGoal.repository.MonthlyGoalRepository;
import blueclub.server.s3.service.S3UploadService;
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
import java.time.temporal.TemporalAdjusters;
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
    private final MonthlyGoalRepository monthlyGoalRepository;

    private static final Integer PAGE_SIZE = 4;

    public void saveDayOffDiary(UserDetails userDetails, UpdateBaseDiaryRequest updateBaseDiaryRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        Diary diary = saveBaseDiary(user, Worktype.DAY_OFF, updateBaseDiaryRequest.getDate());
        diaryRepository.save(diary);
    }

    public GetBoastDiaryResponse saveCaddyDiary(UserDetails userDetails, UpdateCaddyDiaryRequest createCaddyDiaryRequest, List<MultipartFile> multipartFileList) {
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
        return GetBoastDiaryResponse.builder()
                .job(Job.CADDY.getTitle())
                .workAt(createCaddyDiaryRequest.getDate())
                .rank(getRank(createCaddyDiaryRequest.getIncome()).getKey())
                .income(createCaddyDiaryRequest.getIncome())
                .cases(createCaddyDiaryRequest.getRounding())
                .build();
    }

    public GetBoastDiaryResponse saveRiderDiary(UserDetails userDetails, UpdateRiderDiaryRequest createRiderDiaryRequest, List<MultipartFile> multipartFileList) {
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
        return GetBoastDiaryResponse.builder()
                .job(Job.RIDER.getTitle())
                .workAt(createRiderDiaryRequest.getDate())
                .rank(getRank(createRiderDiaryRequest.getIncome()).getKey())
                .income(createRiderDiaryRequest.getIncome())
                .cases(createRiderDiaryRequest.getNumberOfDeliveries())
                .build();
    }

    public GetBoastDiaryResponse saveDayworkerDiary(UserDetails userDetails, UpdateDayworkerDiaryRequest createDayworkerDiaryRequest, List<MultipartFile> multipartFileList) {
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
        return GetBoastDiaryResponse.builder()
                .job(Job.DAYWORKER.getTitle())
                .workAt(createDayworkerDiaryRequest.getDate())
                .rank(getRank(createDayworkerDiaryRequest.getIncome()).getKey())
                .income(createDayworkerDiaryRequest.getIncome())
                .build();
    }

    public void updateDayOffDiary(UserDetails userDetails, Long diaryId) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<Diary> diary = diaryRepository.findById(diaryId);
        if (diary.isEmpty()) {
            throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR);
        }

        if (!diary.get().getWorktype().equals(Worktype.DAY_OFF)) {
            switch (user.getJob()) {
                case CADDY -> caddyRepository.deleteById(diary.get().getCaddy().getId());
                case RIDER -> riderRepository.deleteById(diary.get().getRider().getId());
                case DAYWORKER -> dayworkerRepository.deleteById(diary.get().getDayworker().getId());
            }
            diary.get().unlink();
        }
        diary.get().update(
                Worktype.DAY_OFF,
                null,
                new ArrayList<>(),
                0L,
                0L,
                0L
        );
    }

    public GetBoastDiaryResponse updateCaddyDiary(UserDetails userDetails, Long diaryId, UpdateCaddyDiaryRequest updateCaddyDiaryRequest, List<MultipartFile> multipartFileList) {
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
        return GetBoastDiaryResponse.builder()
                .job(Job.CADDY.getTitle())
                .workAt(updateCaddyDiaryRequest.getDate())
                .rank(getRank(updateCaddyDiaryRequest.getIncome()).getKey())
                .income(updateCaddyDiaryRequest.getIncome())
                .cases(updateCaddyDiaryRequest.getRounding())
                .build();
    }

    public GetBoastDiaryResponse updateRiderDiary(UserDetails userDetails, Long diaryId, UpdateRiderDiaryRequest updateRiderDiaryRequest, List<MultipartFile> multipartFileList) {
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
        return GetBoastDiaryResponse.builder()
                .job(Job.RIDER.getTitle())
                .workAt(updateRiderDiaryRequest.getDate())
                .rank(getRank(updateRiderDiaryRequest.getIncome()).getKey())
                .income(updateRiderDiaryRequest.getIncome())
                .cases(updateRiderDiaryRequest.getNumberOfDeliveries())
                .build();
    }

    public GetBoastDiaryResponse updateDayworkerDiary(UserDetails userDetails, Long diaryId, UpdateDayworkerDiaryRequest updateDayworkerDiaryRequest, List<MultipartFile> multipartFileList) {
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
        return GetBoastDiaryResponse.builder()
                .job(Job.DAYWORKER.getTitle())
                .workAt(updateDayworkerDiaryRequest.getDate())
                .rank(getRank(updateDayworkerDiaryRequest.getIncome()).getKey())
                .income(updateDayworkerDiaryRequest.getIncome())
                .build();
    }

    @Transactional(readOnly = true)
    public Object getDiaryDetails(UserDetails userDetails, String jobTitle, Long diaryId) {
        User user = userFindService.findByUserDetails(userDetails);
        List<Diary> diary = diaryRepository.getDiaryById(diaryId);
        if (diary.isEmpty()) {
            throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR);
        }

        if (diary.get(0).getWorktype().equals(Worktype.DAY_OFF)) {
            return GetDayOffDiaryDetailsResponse.builder()
                    .worktype(Worktype.DAY_OFF.getKey())
                    .build();
        }

        if (Job.CADDY.getTitle().equals(jobTitle.replace(" ", ""))) {
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
        } else if (Job.RIDER.getTitle().equals(jobTitle.replace(" ", ""))) {
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
        } else if (Job.DAYWORKER.getTitle().equals(jobTitle.replace(" ", ""))) {
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
    public GetMonthlyRecordListResponse getMonthlyList(UserDetails userDetails, YearMonth yearMonth, Long id) {
        User user = userFindService.findByUserDetails(userDetails);
        List<Diary> diaryList;
        if (id == -1)
            diaryList = diaryRepository.getMonthlyList(user, yearMonth, LocalDate.now().plusDays(1), PAGE_SIZE);
        else {
            Diary diary = diaryRepository.findById(id)
                    .orElseThrow(() -> new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR));
            diaryList = diaryRepository.getMonthlyList(user, yearMonth, diary.getWorkAt(), PAGE_SIZE);
        }

        List<MonthlyRecord> monthlyRecordList = new ArrayList<>();
        for (Diary diary: diaryList) {
            monthlyRecordList.add(MonthlyRecord.builder()
                            .id(diary.getId())
                            .date(diary.getWorkAt())
                            .worktype(diary.getWorktype().getKey())
                            .income(diary.getIncome())
                            .cases((diary.getWorktype().equals(Worktype.DAY_OFF) || user.getJob().equals(Job.DAYWORKER)) ? null
                                    : (user.getJob().equals(Job.CADDY)) ? diary.getCaddy().getRounding() : diary.getRider().getNumberOfDeliveries())
                    .build());
        }

        return GetMonthlyRecordListResponse.builder()
                .totalDay(diaryRepository.getTotalWorkingDay(user, yearMonth))
                .monthlyRecord(monthlyRecordList)
                .build();
    }

    public GetMonthlyRecordResponse getMonthlyRecord(UserDetails userDetails, YearMonth yearMonth) {
        User user = userFindService.findByUserDetails(userDetails);
        Integer straightWorkingDayLimitMonth, straightWorkingMonth;
        LocalDate now = LocalDate.now();
        Boolean isRenew;

        if (diaryRepository.existsByWorkAt(now)) {
            straightWorkingDayLimitMonth = diaryRepository.getStraightWorkingDayLimitMonth(user, now);
            straightWorkingMonth = diaryRepository.getStraightWorkingMonth(user, now);
            isRenew = diaryRepository.isRenew(user, now);
        }
        else {
            if (now.equals(now.with(TemporalAdjusters.firstDayOfMonth())))
                straightWorkingDayLimitMonth = 0;
            else
                straightWorkingDayLimitMonth = diaryRepository.getStraightWorkingDayLimitMonth(user, now.minusDays(1));
            straightWorkingMonth = diaryRepository.getStraightWorkingMonth(user, now.minusDays(1));
            isRenew = diaryRepository.isRenew(user, now.minusDays(1));
        }

        Optional<MonthlyGoal> monthlyGoal = monthlyGoalRepository.findByUserAndYearMonth(user, yearMonth);
        if (monthlyGoal.isEmpty()) {
            Long recentMonthlyGoal = monthlyGoalRepository.getRecentMonthlyGoal(user);
            // 첫 사용자에 대한 예외 처리
            if (recentMonthlyGoal == -1) {
                return GetMonthlyRecordResponse.builder()
                        .totalDay(diaryRepository.getTotalWorkingDay(user, yearMonth))
                        .straightDay(straightWorkingDayLimitMonth)
                        .isRenew(isRenew)
                        .straightMonth(straightWorkingMonth)
                        .build();
            }
            MonthlyGoal newMonthlyGoal = MonthlyGoal.builder()
                    .yearMonth(yearMonth)
                    .targetIncome(recentMonthlyGoal)
                    .user(user)
                    .build();
            monthlyGoalRepository.save(newMonthlyGoal);

            Long totalIncome = getTotalMonthlyIncome(user, yearMonth);
            return GetMonthlyRecordResponse.builder()
                    .totalDay(diaryRepository.getTotalWorkingDay(user, yearMonth))
                    .straightDay(straightWorkingDayLimitMonth)
                    .isRenew(isRenew)
                    .straightMonth(straightWorkingMonth)
                    .targetIncome(recentMonthlyGoal)
                    .totalIncome(totalIncome)
                    .progress((int) Math.floor((double) totalIncome/recentMonthlyGoal*100))
                    .build();
        }
        Long totalIncome = getTotalMonthlyIncome(user, yearMonth);
        return GetMonthlyRecordResponse.builder()
                .totalDay(diaryRepository.getTotalWorkingDay(user, yearMonth))
                .straightDay(straightWorkingDayLimitMonth)
                .isRenew(isRenew)
                .straightMonth(straightWorkingMonth)
                .targetIncome(monthlyGoal.get().getTargetIncome())
                .totalIncome(totalIncome)
                .progress((int) Math.floor((double) totalIncome/monthlyGoal.get().getTargetIncome()*100))
                .build();
    }

    @Transactional(readOnly = true)
    public Long getTotalMonthlyIncome(User user, YearMonth yearMonth) {
        return diaryRepository.getTotalMonthlyIncome(user, yearMonth);
    }

    @Transactional(readOnly = true)
    public GetBoastDiaryResponse getBoastDiary(UserDetails userDetails, Long id) {
        User user = userFindService.findByUserDetails(userDetails);
        Diary diary = diaryRepository.findById(id)
                .orElseThrow(() -> new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR));
        if (!diary.getUser().equals(user))
            throw new BaseException(BaseResponseStatus.DIARY_USER_NOT_MATCH_ERROR);

        return getBoastDiaryResponseByJob(user.getJob(), diary);
    }

    private Diary saveBaseDiary(User user, Worktype worktype, LocalDate workAt) {
        return Diary.builder()
                .worktype(worktype)
                .workAt(workAt)
                .user(user)
                .build();
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

    private Rank getRank(Long income) {
        if (income >= 190000)
            return Rank.UNDER_FIVE_PERCENT;
        else if (income >= 140000)
            return Rank.UNDER_THIRTY_PERCENT;
        return Rank.ELSE;
    }

    private GetBoastDiaryResponse getBoastDiaryResponseByJob(Job job, Diary diary) {
        if (job.equals(Job.CADDY)) {
            return GetBoastDiaryResponse.builder()
                    .job(job.getTitle())
                    .workAt(diary.getWorkAt())
                    .rank(getRank(diary.getIncome()).getKey())
                    .income(diary.getIncome())
                    .cases(diary.getCaddy().getRounding())
                    .build();
        } else if (job.equals(Job.RIDER)) {
            return GetBoastDiaryResponse.builder()
                    .job(job.getTitle())
                    .workAt(diary.getWorkAt())
                    .rank(getRank(diary.getIncome()).getKey())
                    .income(diary.getIncome())
                    .cases(diary.getRider().getNumberOfDeliveries())
                    .build();
        } else if (job.equals(Job.DAYWORKER)) {
            return GetBoastDiaryResponse.builder()
                    .job(job.getTitle())
                    .workAt(diary.getWorkAt())
                    .rank(getRank(diary.getIncome()).getKey())
                    .income(diary.getIncome())
                    .build();
        }
        throw new BaseException(BaseResponseStatus.JOB_NOT_FOUND_ERROR);
    }
}
