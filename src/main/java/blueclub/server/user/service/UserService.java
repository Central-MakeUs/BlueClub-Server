package blueclub.server.user.service;

import blueclub.server.auth.service.JwtService;
import blueclub.server.global.service.S3UploadService;
import blueclub.server.user.domain.Job;
import blueclub.server.user.domain.User;
import blueclub.server.user.dto.request.AddUserDetailsRequest;
import blueclub.server.user.dto.request.UpdateAgreementRequest;
import blueclub.server.user.dto.request.UpdateUserDetailsRequest;
import blueclub.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserFindService userFindService;
    private final S3UploadService s3UploadService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public void addUserDetails(UserDetails userDetails, AddUserDetailsRequest addUserDetailsRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        user.addDetails(
                addUserDetailsRequest.nickname(),
                Job.findByTitle(addUserDetailsRequest.job().replace(" ", "")),
                addUserDetailsRequest.monthlyTargetIncome(),
                addUserDetailsRequest.tosAgree()
        );
    }

    public void updateUserDetails(UserDetails userDetails, UpdateUserDetailsRequest updateUserDetailsRequest, MultipartFile multipartFile) {
        User user = userFindService.findByUserDetails(userDetails);
        user.updateDetails(
                updateUserDetailsRequest.nickname(),
                Job.valueOf(updateUserDetailsRequest.job().replace(" ", "")),
                updateUserDetailsRequest.monthlyTargetIncome(),
                uploadProfileImage(user, multipartFile)
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

    // user 관련 정보 삭제 (User, RefreshToken)
    private void deleteUser(User user) {
        jwtService.deleteRefreshToken(user.getId());
        userRepository.delete(user);
    }

    private String uploadProfileImage(User user, MultipartFile multipartFile) {
        String fileName = user.getProfileImage();
        if (multipartFile == null)
            return fileName;

        try { // 파일 업로드
            fileName = s3UploadService.upload(multipartFile, "profile"); // S3 버킷의 images 디렉토리 안에 저장됨
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return fileName;
    }
}
