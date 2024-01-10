package blueclub.server.user.service;

import blueclub.server.auth.domain.CustomOAuth2User;
import blueclub.server.auth.domain.SocialType;
import blueclub.server.auth.service.JwtService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.domain.Job;
import blueclub.server.user.domain.User;
import blueclub.server.user.dto.request.AddDetailsRequest;
import blueclub.server.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserFindService userFindService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    public void addUserDetails(UserDetails userDetails, AddDetailsRequest addDetailsRequest) {
        User user = userFindService.findByUserDetails(userDetails);
        user.addDetails(
                addDetailsRequest.nickname(),
                Job.findByTitle(addDetailsRequest.jobTitle()),
                addDetailsRequest.jobStart(),
                addDetailsRequest.tosAgree()
        );
    }

    public void withdrawalUser(UserDetails userDetails, String socialTypeName) {
        User user = userFindService.findByUserDetails(userDetails);
        SocialType socialType = SocialType.valueOf(socialTypeName.toUpperCase());
        deleteUser(user);

        if (socialType.equals(SocialType.KAKAO)) {
            sendWithdrawalRequest(null, socialType, user.getSocialAccessToken());
        }
        if (socialType.equals(SocialType.NAVER)) {
            StringBuilder stringBuilder = new StringBuilder();
            String data = stringBuilder.append("client_id=").append(naverClientId)
                            .append("&client_secret=").append(naverClientSecret)
                            .append("&access_token=").append(user.getSocialAccessToken())
                            .append("&service_provider=").append(socialType)
                            .append("&grant_type=delete").toString();
            sendWithdrawalRequest(data, socialType, null);
        }
    }

    private void sendWithdrawalRequest(String data, SocialType socialType, String accessToken) {
        String appleWithdrawalUrl = "https://appleid.apple.com/auth/revoke";
        String naverWithdrawalUrl = "https://nid.naver.com/oauth2.0/token";
        String kakaoWithdrawalUrl = "https://kapi.kakao.com/v1/user/unlink";

        RestTemplate restTemplate = new RestTemplate();
        String WithdrawalUrl = "";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(data, headers);

        switch (socialType) {
            case APPLE -> WithdrawalUrl = appleWithdrawalUrl;
            case NAVER -> WithdrawalUrl = naverWithdrawalUrl;
            case KAKAO -> {
                WithdrawalUrl = kakaoWithdrawalUrl;
                headers.setBearerAuth(accessToken);
            }
        }

        ResponseEntity<String> responseEntity = restTemplate.exchange(WithdrawalUrl, HttpMethod.POST, entity, String.class);

        HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
        if (!statusCode.is2xxSuccessful())
            throw new BaseException(BaseResponseStatus.BAD_GATEWAY);
    }

    public void addSocialAccessToken(CustomOAuth2User oAuth2User) {
        User user = userFindService.findById(oAuth2User.getId());
        user.addSocialAccessToken(oAuth2User.getOauthAccessToken());
    }

    // user 관련 정보 삭제 (User, RefreshToken)
    private void deleteUser(User user) {
        jwtService.deleteRefreshToken(user.getId());
        userRepository.delete(user);
    }
}
