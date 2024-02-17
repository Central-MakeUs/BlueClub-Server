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
import blueclub.server.file.service.S3UploadService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.monthlyGoal.domain.MonthlyGoal;
import blueclub.server.monthlyGoal.dto.request.UpdateMonthlyGoalRequest;
import blueclub.server.monthlyGoal.repository.MonthlyGoalRepository;
import blueclub.server.monthlyGoal.service.MonthlyGoalService;
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
import java.time.format.DateTimeFormatter;
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
    private final MonthlyGoalService monthlyGoalService;
    private final DiaryRepository diaryRepository;
    private final CaddyRepository caddyRepository;
    private final RiderRepository riderRepository;
    private final DayworkerRepository dayworkerRepository;
    private final MonthlyGoalRepository monthlyGoalRepository;

    public GetDiaryIdResponse saveDayOffDiary(UserDetails userDetails, UpdateBaseDiaryRequest updateBaseDiaryRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        isValidDate(user, LocalDate.parse(updateBaseDiaryRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-M-d")));
        Diary diary = diaryRepository.save(saveBaseDiary(user, Worktype.DAY_OFF, LocalDate.parse(updateBaseDiaryRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-M-d")), user.getJob()));
        return new GetDiaryIdResponse(diary.getId());
    }

    public GetDiaryIdResponse saveCaddyDiary(UserDetails userDetails, UpdateCaddyDiaryRequest createCaddyDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        isValidDate(user, LocalDate.parse(createCaddyDiaryRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-M-d")));
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByValue(createCaddyDiaryRequest.getWorktype()), createCaddyDiaryRequest.getMemo(),
                imageUrlList, createCaddyDiaryRequest.getIncome(), createCaddyDiaryRequest.getExpenditure(),
                createCaddyDiaryRequest.getSaving(), LocalDate.parse(createCaddyDiaryRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-M-d")), user.getJob());
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
        return new GetDiaryIdResponse(savedDiary.getId());
    }

    public GetDiaryIdResponse saveRiderDiary(UserDetails userDetails, UpdateRiderDiaryRequest createRiderDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        isValidDate(user, LocalDate.parse(createRiderDiaryRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-M-d")));
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByValue(createRiderDiaryRequest.getWorktype()), createRiderDiaryRequest.getMemo(),
                imageUrlList, createRiderDiaryRequest.getIncome(), createRiderDiaryRequest.getExpenditure(),
                createRiderDiaryRequest.getSaving(), LocalDate.parse(createRiderDiaryRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-M-d")), user.getJob());
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
        return new GetDiaryIdResponse(savedDiary.getId());
    }

    public GetDiaryIdResponse saveDayworkerDiary(UserDetails userDetails, UpdateDayworkerDiaryRequest createDayworkerDiaryRequest, List<MultipartFile> multipartFileList) {
        User user = userFindService.findByUserDetails(userDetails);
        isValidDate(user, LocalDate.parse(createDayworkerDiaryRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-M-d")));
        List<String> imageUrlList = uploadDiaryImage(multipartFileList);
        Diary diary = saveDiary(user, Worktype.findByValue(createDayworkerDiaryRequest.getWorktype()), createDayworkerDiaryRequest.getMemo(),
                imageUrlList, createDayworkerDiaryRequest.getIncome(), createDayworkerDiaryRequest.getExpenditure(),
                createDayworkerDiaryRequest.getSaving(), LocalDate.parse(createDayworkerDiaryRequest.getDate(), DateTimeFormatter.ofPattern("yyyy-M-d")), user.getJob());
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
        return new GetDiaryIdResponse(savedDiary.getId());
    }

    public GetDiaryIdResponse updateDayOffDiary(UserDetails userDetails, Long diaryId) {
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
                0L,
                user.getJob()
        );
        return new GetDiaryIdResponse(diary.get().getId());
    }

    public GetDiaryIdResponse updateCaddyDiary(UserDetails userDetails, Long diaryId, UpdateCaddyDiaryRequest updateCaddyDiaryRequest, List<MultipartFile> multipartFileList) {
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
                Worktype.findByValue(updateCaddyDiaryRequest.getWorktype()),
                updateCaddyDiaryRequest.getMemo(),
                updateImageUrlList,
                updateCaddyDiaryRequest.getIncome(),
                updateCaddyDiaryRequest.getExpenditure(),
                updateCaddyDiaryRequest.getSaving(),
                user.getJob());
        if (caddy == null) {
            Caddy newCaddy = Caddy.builder()
                    .diary(diary.get())
                    .rounding(updateCaddyDiaryRequest.getRounding())
                    .caddyFee(updateCaddyDiaryRequest.getCaddyFee())
                    .overFee(updateCaddyDiaryRequest.getOverFee())
                    .topdressing(updateCaddyDiaryRequest.getTopdressing())
                    .build();
            caddyRepository.save(newCaddy);
            diary.get().setCaddy(newCaddy);
            return new GetDiaryIdResponse(diary.get().getId());
        }
        caddy.update(
                updateCaddyDiaryRequest.getRounding(),
                updateCaddyDiaryRequest.getCaddyFee(),
                updateCaddyDiaryRequest.getOverFee(),
                updateCaddyDiaryRequest.getTopdressing());
        return new GetDiaryIdResponse(diary.get().getId());
    }

    public GetDiaryIdResponse updateRiderDiary(UserDetails userDetails, Long diaryId, UpdateRiderDiaryRequest updateRiderDiaryRequest, List<MultipartFile> multipartFileList) {
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
                Worktype.findByValue(updateRiderDiaryRequest.getWorktype()),
                updateRiderDiaryRequest.getMemo(),
                updateImageUrlList,
                updateRiderDiaryRequest.getIncome(),
                updateRiderDiaryRequest.getExpenditure(),
                updateRiderDiaryRequest.getSaving(),
                user.getJob());
        if (rider == null) {
            Rider newRider = Rider.builder()
                    .diary(diary.get())
                    .numberOfDeliveries(updateRiderDiaryRequest.getNumberOfDeliveries())
                    .incomeOfDeliveries(updateRiderDiaryRequest.getIncomeOfDeliveries())
                    .numberOfPromotions(updateRiderDiaryRequest.getNumberOfPromotions())
                    .incomeOfPromotions(updateRiderDiaryRequest.getIncomeOfPromotions())
                    .build();
            riderRepository.save(newRider);
            diary.get().setRider(newRider);
            return new GetDiaryIdResponse(diary.get().getId());
        }
        rider.update(
                updateRiderDiaryRequest.getNumberOfDeliveries(),
                updateRiderDiaryRequest.getIncomeOfDeliveries(),
                updateRiderDiaryRequest.getNumberOfPromotions(),
                updateRiderDiaryRequest.getIncomeOfPromotions());
        return new GetDiaryIdResponse(diary.get().getId());
    }

    public GetDiaryIdResponse updateDayworkerDiary(UserDetails userDetails, Long diaryId, UpdateDayworkerDiaryRequest updateDayworkerDiaryRequest, List<MultipartFile> multipartFileList) {
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
                Worktype.findByValue(updateDayworkerDiaryRequest.getWorktype()),
                updateDayworkerDiaryRequest.getMemo(),
                updateImageUrlList,
                updateDayworkerDiaryRequest.getIncome(),
                updateDayworkerDiaryRequest.getExpenditure(),
                updateDayworkerDiaryRequest.getSaving(),
                user.getJob());
        if (dayworker == null) {
            Dayworker newDayworker = Dayworker.builder()
                    .diary(diary.get())
                    .place(updateDayworkerDiaryRequest.getPlace())
                    .dailyWage(updateDayworkerDiaryRequest.getDailyWage())
                    .typeOfJob(updateDayworkerDiaryRequest.getTypeOfJob())
                    .numberOfWork(updateDayworkerDiaryRequest.getNumberOfWork())
                    .build();
            dayworkerRepository.save(newDayworker);
            diary.get().setDayworker(newDayworker);
            return new GetDiaryIdResponse(diary.get().getId());
        }
        dayworker.update(
                updateDayworkerDiaryRequest.getPlace(),
                updateDayworkerDiaryRequest.getDailyWage(),
                updateDayworkerDiaryRequest.getTypeOfJob(),
                updateDayworkerDiaryRequest.getNumberOfWork());
        return new GetDiaryIdResponse(diary.get().getId());
    }

    @Transactional(readOnly = true)
    public Object getDiaryDetails(UserDetails userDetails, String jobTitle, Long diaryId) {
        User user = userFindService.findByUserDetails(userDetails);
        if (!user.getJob().equals(Job.findByTitle(jobTitle)))
            throw new BaseException(BaseResponseStatus.JOB_USER_NOT_MATCH_ERROR);

        List<Diary> diary = diaryRepository.getDiaryById(user, diaryId);
        if (diary.isEmpty())
            throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR);

        return getDiaryDetailsSplitByCase(jobTitle, diary.get(0));
    }

    @Transactional(readOnly = true)
    public Object getDiaryDetailsByDate(UserDetails userDetails, String jobTitle, LocalDate date) {
        User user = userFindService.findByUserDetails(userDetails);
        if (!user.getJob().equals(Job.findByTitle(jobTitle)))
            throw new BaseException(BaseResponseStatus.JOB_USER_NOT_MATCH_ERROR);

        List<Diary> diary = diaryRepository.getDiaryByDate(user, date);
        if (diary.isEmpty())
            return null;

        return getDiaryDetailsSplitByCase(jobTitle, diary.get(0));
    }

    public void deleteDiary(UserDetails userDetails, Long diaryId) {
        User user = userFindService.findByUserDetails(userDetails);
        Optional<Diary> diary = diaryRepository.findById(diaryId);
        if (diary.isEmpty())
            throw new BaseException(BaseResponseStatus.DIARY_NOT_FOUND_ERROR);
        if (!diary.get().getUser().equals(user))
            throw new BaseException(BaseResponseStatus.DIARY_USER_NOT_MATCH_ERROR);
        diaryRepository.deleteById(diaryId);
    }

    @Transactional(readOnly = true)
    public GetMonthlyRecordListResponse getMonthlyList(UserDetails userDetails, YearMonth yearMonth) {
        User user = userFindService.findByUserDetails(userDetails);
        List<Diary> diaryList = diaryRepository.getMonthlyList(user, yearMonth);

        List<MonthlyRecord> monthlyRecordList = new ArrayList<>();
        for (Diary diary: diaryList) {
            monthlyRecordList.add(MonthlyRecord.builder()
                            .id(diary.getId())
                            .date(diary.getWorkAt())
                            .worktype(diary.getWorktype().getValue())
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
            Long recentMonthlyGoal = monthlyGoalRepository.getRecentMonthlyGoal(user, yearMonth);
            Long totalIncome = getTotalMonthlyIncome(user, yearMonth);

            // 첫 사용자에 대한 예외 처리
            if (recentMonthlyGoal == -1L) {
                return GetMonthlyRecordResponse.builder()
                        .totalDay(diaryRepository.getTotalWorkingDay(user, yearMonth))
                        .straightDay(straightWorkingDayLimitMonth)
                        .isRenew(isRenew)
                        .straightMonth(straightWorkingMonth)
                        .targetIncome(0L)
                        .totalIncome(totalIncome)
                        .progress(0)
                        .build();
            }

            monthlyGoalService.updateMonthlyGoal(userDetails, UpdateMonthlyGoalRequest.builder()
                    .yearMonth(yearMonth.toString())
                    .monthlyTargetIncome(recentMonthlyGoal)
                    .build());

            return GetMonthlyRecordResponse.builder()
                    .totalDay(diaryRepository.getTotalWorkingDay(user, yearMonth))
                    .straightDay(straightWorkingDayLimitMonth)
                    .isRenew(isRenew)
                    .straightMonth(straightWorkingMonth)
                    .targetIncome(recentMonthlyGoal)
                    .totalIncome(totalIncome)
                    .progress((int) Math.min(Math.floor((double) totalIncome/recentMonthlyGoal*100), 100))
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
                .progress((int) Math.min(Math.floor((double) totalIncome/monthlyGoal.get().getTargetIncome()*100), 100))
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
        if (diary.getWorktype().equals(Worktype.DAY_OFF))
            throw new BaseException(BaseResponseStatus.CANT_BOAST_DIARY_ERROR);

        return getBoastDiaryResponseByJob(user.getJob(), diary);
    }

    private Diary saveBaseDiary(User user, Worktype worktype, LocalDate workAt, Job job) {
        return Diary.builder()
                .worktype(worktype)
                .workAt(workAt)
                .job(job)
                .user(user)
                .build();
    }

    private Diary saveDiary(User user, Worktype worktype, String memo, List<String> imageUrlList, Long income, Long expenditure, Long saving, LocalDate workAt, Job job) {
        return Diary.builder()
                .worktype(worktype)
                .memo(memo)
                .image(imageUrlList)
                .income(income)
                .expenditure(expenditure)
                .saving(saving)
                .workAt(workAt)
                .user(user)
                .job(job)
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

    private Object getDiaryDetailsSplitByCase(String jobTitle, Diary diary) {
        if (diary.getWorktype().equals(Worktype.DAY_OFF))
            return getDefaultDiaryDetails(jobTitle, diary);

        if (Job.CADDY.getTitle().equals(jobTitle.replace(" ", ""))) {
            return GetCaddyDiaryDetailsResponse.builder()
                    .id(diary.getId())
                    .worktype(diary.getWorktype().getValue())
                    .date(diary.getWorkAt().toString())
                    .memo(diary.getMemo())
                    .imageUrlList(diary.getImage())
                    .income(diary.getIncome())
                    .expenditure(diary.getExpenditure())
                    .saving(diary.getSaving())
                    .rounding(diary.getCaddy().getRounding())
                    .caddyFee(diary.getCaddy().getCaddyFee())
                    .overFee(diary.getCaddy().getOverFee())
                    .topdressing(diary.getCaddy().getTopdressing())
                    .build();
        } else if (Job.RIDER.getTitle().equals(jobTitle.replace(" ", ""))) {
            return GetRiderDiaryDetailsResponse.builder()
                    .id(diary.getId())
                    .worktype(diary.getWorktype().getValue())
                    .date(diary.getWorkAt().toString())
                    .memo(diary.getMemo())
                    .imageUrlList(diary.getImage())
                    .income(diary.getIncome())
                    .expenditure(diary.getExpenditure())
                    .saving(diary.getSaving())
                    .incomeOfDeliveries(diary.getRider().getIncomeOfDeliveries())
                    .numberOfDeliveries(diary.getRider().getNumberOfDeliveries())
                    .incomeOfPromotions(diary.getRider().getIncomeOfPromotions())
                    .numberOfPromotions(diary.getRider().getNumberOfPromotions())
                    .build();
        } else if (Job.DAYWORKER.getTitle().equals(jobTitle.replace(" ", ""))) {
            return GetDayworkerDiaryDetailsResponse.builder()
                    .id(diary.getId())
                    .worktype(diary.getWorktype().getValue())
                    .date(diary.getWorkAt().toString())
                    .memo(diary.getMemo())
                    .imageUrlList(diary.getImage())
                    .income(diary.getIncome())
                    .expenditure(diary.getExpenditure())
                    .saving(diary.getSaving())
                    .place(diary.getDayworker().getPlace())
                    .dailyWage(diary.getDayworker().getDailyWage())
                    .typeOfJob(diary.getDayworker().getTypeOfJob())
                    .numberOfWork(diary.getDayworker().getNumberOfWork())
                    .build();
        }
        throw new BaseException(BaseResponseStatus.JOB_NOT_FOUND_ERROR);
    }

    private Object getDefaultDiaryDetails(String jobTitle, Diary diary) {
        if (Job.CADDY.getTitle().equals(jobTitle.replace(" ", ""))) {
            return GetCaddyDiaryDetailsResponse.builder()
                    .id(diary.getId())
                    .worktype(Worktype.DAY_OFF.getValue())
                    .date(diary.getWorkAt().toString())
                    .memo("")
                    .imageUrlList(new ArrayList<>())
                    .income(0L)
                    .expenditure(0L)
                    .saving(0L)
                    .rounding(0L)
                    .caddyFee(0L)
                    .overFee(0L)
                    .topdressing(false)
                    .build();
        } else if (Job.RIDER.getTitle().equals(jobTitle.replace(" ", ""))) {
            return GetRiderDiaryDetailsResponse.builder()
                    .id(diary.getId())
                    .worktype(Worktype.DAY_OFF.getValue())
                    .date(diary.getWorkAt().toString())
                    .memo("")
                    .imageUrlList(new ArrayList<>())
                    .income(0L)
                    .expenditure(0L)
                    .saving(0L)
                    .incomeOfDeliveries(0L)
                    .numberOfDeliveries(0L)
                    .incomeOfPromotions(0L)
                    .numberOfPromotions(0L)
                    .build();
        } else if (Job.DAYWORKER.getTitle().equals(jobTitle.replace(" ", ""))) {
            return GetDayworkerDiaryDetailsResponse.builder()
                    .id(diary.getId())
                    .worktype(Worktype.DAY_OFF.getValue())
                    .date(diary.getWorkAt().toString())
                    .memo("")
                    .imageUrlList(new ArrayList<>())
                    .income(0L)
                    .expenditure(0L)
                    .saving(0L)
                    .place("")
                    .dailyWage(0L)
                    .typeOfJob("")
                    .numberOfWork(0.0)
                    .build();
        }
        throw new BaseException(BaseResponseStatus.JOB_NOT_FOUND_ERROR);
    }

    private void isValidDate(User user, LocalDate date) {
        if (diaryRepository.existsByUserAndWorkAtAndJob(user, date, user.getJob()))
            throw new BaseException(BaseResponseStatus.DIARY_ALREADY_EXISTS_ERROR);
    }
}
