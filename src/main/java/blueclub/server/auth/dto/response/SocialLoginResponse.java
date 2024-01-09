package blueclub.server.auth.dto.response;

import lombok.Builder;

@Builder
public record SocialLoginResponse (
        Long id,
        String accessToken,
        String refreshToken
) {
}
