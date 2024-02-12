package blueclub.server.auth.service;

import blueclub.server.auth.domain.Role;
import blueclub.server.auth.domain.SocialType;
import blueclub.server.auth.dto.request.SocialLoginRequest;
import blueclub.server.auth.dto.response.SocialLoginResponse;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.domain.User;
import blueclub.server.user.repository.UserRepository;
import blueclub.server.user.service.UserFindService;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final JwtService jwtService;
    private final FcmTokenService fcmTokenService;
    private final UserFindService userFindService;
    private final UserRepository userRepository;

    private static final Integer RANDOM_NICKNAME_LENGTH = 3;

    public SocialLoginResponse socialLogin(SocialLoginRequest socialLoginRequest) {
        if (userRepository.existsBySocialTypeAndSocialId(SocialType.valueOf(socialLoginRequest.socialType()), socialLoginRequest.socialId())) {
            User user = isRegister(socialLoginRequest);
            String refreshToken = jwtService.createRefreshToken();
            jwtService.updateRefreshToken(user.getId(), refreshToken);
            fcmTokenService.saveFcmToken(user.getId(), socialLoginRequest.fcmToken());

            // 유저 정보, accesstoken, refreshtoken 전달 필요
            if (user.getRole().equals(Role.USER)) {
                return SocialLoginResponse.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .nickname(user.getNickname())
                        .phoneNumber(user.getPhoneNumber())
                        .profileImage(user.getProfileImage())
                        .job(user.getJob().getTitle())
                        .monthlyTargetIncome(user.getMonthlyTargetIncome())
                        .tosAgree(user.getTosAgree())
                        .role(user.getRole().getTitle())
                        .socialType(user.getSocialType().name())
                        .socialId(user.getSocialId())
                        .accessToken(jwtService.createAccessToken(user.getSocialId()))
                        .refreshToken(refreshToken)
                        .build();
            }
            return SocialLoginResponse.builder()
                    .id(user.getId())
                    .nickname(user.getNickname())
                    .role(user.getRole().getTitle())
                    .accessToken(jwtService.createAccessToken(user.getSocialId()))
                    .refreshToken(refreshToken)
                    .build();
        }

        User user = isRegister(socialLoginRequest);
        String refreshToken = jwtService.createRefreshToken();
        jwtService.updateRefreshToken(user.getId(), refreshToken);

        return SocialLoginResponse.builder()
                .id(user.getId())
                .nickname(user.getNickname())
                .accessToken(jwtService.createAccessToken(user.getSocialId()))
                .refreshToken(refreshToken)
                .build();
    }

    public boolean checkNickname(UserDetails userDetails, String nickname) {
        User user = userFindService.findByUserDetails(userDetails);
        if (user.getRole().equals(Role.USER) && user.getNickname().equals(nickname))
            return false;
        return userRepository.existsByNicknameAndRole(nickname, Role.USER).equals(true);
    }

    public void logout(UserDetails userDetails) {
        User user = userFindService.findByUserDetails(userDetails);
        jwtService.deleteRefreshToken(user.getId());
        fcmTokenService.deleteFcmToken(user.getId());
    }

    public void addFcmToken(UserDetails userDetails, String fcmToken) {
        User user = userFindService.findByUserDetails(userDetails);
        fcmTokenService.saveFcmToken(user.getId(), fcmToken);
    }

    // 분기 처리, 유저 정보 반환
    private User isRegister(SocialLoginRequest socialLoginRequest) {
        SocialType socialType = SocialType.valueOf(socialLoginRequest.socialType().toUpperCase());
        Optional<User> user = userRepository.findBySocialTypeAndSocialId(socialType, socialLoginRequest.socialId());
        // 재로그인
        if (user.isPresent()) {
            return user.get();
        }
        // 회원가입
        User newUser = createUser(socialLoginRequest.socialId(), socialType, socialLoginRequest.name(), socialLoginRequest.nickname(),
                socialLoginRequest.email(), socialLoginRequest.phoneNumber(), socialLoginRequest.profileImage());
        userRepository.save(newUser);
        return newUser;
    }

    private User createUser(String socialId, SocialType socialType, String name, String nickname, String email, String phoneNumber, String profileImage) {
        // 닉네임 이모티콘, 특수기호 존재 시 삭제
        EmojiParser.removeAllEmojis(nickname);
        nickname = nickname.replaceAll("[^ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]", "");
        // 닉네임 8자리 이상 시, 7자리로 자르기
        if (nickname.length() > 7) {
            nickname = nickname.substring(0, 7);
        }
        // 분기 처리, 닉네임 중복 또는 공백 시 뒤에 랜덤값 추가
        if (nickname.isBlank() || checkNickname(nickname))
            nickname = createTemporaryNickname(nickname);
        return User.builder()
                .socialId(socialId)
                .socialType(socialType)
                .name(name)
                .nickname(nickname)
                .email(email)
                .phoneNumber(phoneNumber)
                .profileImage(profileImage)
                .role(Role.GUEST)
                .build();
    }

    private String createTemporaryNickname(String nickname) {
        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder
                .append(nickname)
                .append(RandomStringUtils.randomAlphabetic(RANDOM_NICKNAME_LENGTH)).toString();
    }

    private boolean checkNickname(String nickname) {
        return userRepository.existsByNicknameAndRole(nickname, Role.USER).equals(true);
    }
}