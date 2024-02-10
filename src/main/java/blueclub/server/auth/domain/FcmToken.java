package blueclub.server.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@AllArgsConstructor
@Builder
@RedisHash("FcmToken")
public class FcmToken {
    public static final Long DEFAULT_TTL = 1209600000L;

    @Id
    private Long id;

    @Indexed
    private String token;

    @TimeToLive
    private Long expiration;

    public static FcmToken createFcmToken(Long userId, String token) {
        return FcmToken.builder()
                .id(userId)
                .token(token)
                .expiration(DEFAULT_TTL)
                .build();
    }
}
