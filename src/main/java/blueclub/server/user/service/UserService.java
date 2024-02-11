package blueclub.server.user.service;

import blueclub.server.auth.service.JwtService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.file.service.S3UploadService;
import blueclub.server.monthlyGoal.service.MonthlyGoalService;
import blueclub.server.user.domain.Job;
import blueclub.server.user.domain.User;
import blueclub.server.user.dto.request.AddUserDetailsRequest;
import blueclub.server.user.dto.request.UpdateAgreementRequest;
import blueclub.server.user.dto.request.UpdateUserDetailsRequest;
import blueclub.server.user.dto.response.GetAgreementResponse;
import blueclub.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.YearMonth;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserFindService userFindService;
    private final MonthlyGoalService monthlyGoalService;
    private final S3UploadService s3UploadService;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private static final String IMAGE_FORMAT = "(.*?)\\.(jpg|jpeg|png|gif|bmp)$";

    public void addUserDetails(UserDetails userDetails, AddUserDetailsRequest addUserDetailsRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        user.addDetails(
                addUserDetailsRequest.nickname(),
                Job.findByTitle(addUserDetailsRequest.job().replace(" ", "")),
                addUserDetailsRequest.monthlyTargetIncome(),
                addUserDetailsRequest.tosAgree()
        );
        monthlyGoalService.saveMonthlyGoal(user, YearMonth.now(), addUserDetailsRequest.monthlyTargetIncome());
    }

    public void updateUserDetails(UserDetails userDetails, UpdateUserDetailsRequest updateUserDetailsRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        user.updateDetails(
                updateUserDetailsRequest.nickname(),
                Job.findByTitle(updateUserDetailsRequest.job().replace(" ", "")),
                updateUserDetailsRequest.monthlyTargetIncome()
        );
    }

    public void withdrawUser(UserDetails userDetails) {
        User user = userFindService.findByUserDetails(userDetails);
        deleteUser(user);
    }

    public void updateAgreement(UserDetails userDetails, UpdateAgreementRequest updateAgreementRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        user.updateAgreement(updateAgreementRequest.tosAgree(), updateAgreementRequest.pushAgree());
    }

    @Transactional(readOnly = true)
    public GetAgreementResponse getAgreement(UserDetails userDetails) {
        User user = userFindService.findByUserDetails(userDetails);
        return GetAgreementResponse.builder()
                .tosAgree(user.getTosAgree())
                .pushAgree(user.getPushAgree())
                .build();
    }

    public void uploadProfileImage(UserDetails userDetails, MultipartFile multipartFile) {
        User user = userFindService.findByUserDetails(userDetails);
        String fileName;

        if (multipartFile == null || !multipartFile.getOriginalFilename().matches(IMAGE_FORMAT))
            throw new BaseException(BaseResponseStatus.INVALID_FILE);

        try { // 파일 업로드
            fileName = s3UploadService.upload(multipartFile, "profile"); // S3 버킷의 images 디렉토리 안에 저장됨
        } catch (IOException e) {
            throw new BaseException(BaseResponseStatus.BAD_GATEWAY);
        }
        user.updateProfileImage(fileName);
    }

    // user 관련 정보 삭제 (User, RefreshToken)
    private void deleteUser(User user) {
        jwtService.deleteRefreshToken(user.getId());
        userRepository.delete(user);
    }
}
