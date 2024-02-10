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
@RedisHash("refreshToken")
public class RefreshToken {

    public static final Long DEFAULT_TTL = 1209600000L;

    @Id
    private Long id;

    @Indexed
    private String token;

    @TimeToLive
    private Long expiration;

    public static RefreshToken createRefreshToken(Long userId, String token) {
        return RefreshToken.builder()
                .id(userId)
                .token(token)
                .expiration(DEFAULT_TTL)
                .build();
    }

    public void updateRefreshToken(String token) {
        this.token = token;
    }
}
