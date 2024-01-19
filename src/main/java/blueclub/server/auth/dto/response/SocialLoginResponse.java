package blueclub.server.auth.dto.response;

import lombok.Builder;

@Builder
public record SocialLoginResponse(
        Long id,

        String email,

        String name,

        String nickname,

        String phoneNumber,

        String profileImage,

        String job,

        Long monthlyTargetIncome,

        Boolean tosAgree,

        String role,

        String socialType,

        String socialId,

        String accessToken,

        String refreshToken
) {
}
