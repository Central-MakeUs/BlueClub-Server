package blueclub.server.auth.dto.request;

import lombok.Builder;

@Builder
public record SocialLoginRequest(
        String socialId,
        String socialType,
        String name,
        String nickname,
        String email,
        String phoneNumber,
        String profileImage
) {
}
