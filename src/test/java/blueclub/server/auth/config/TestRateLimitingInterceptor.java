package blueclub.server.auth.config;

import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestRateLimitingInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> lastAccessMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String clientIp = getClientIpFromRequest(request);
        Bucket targetBucket = lastAccessMap.getOrDefault(clientIp, createBucket());

        if (targetBucket.tryConsume(1)) {
            lastAccessMap.put(clientIp, targetBucket);
            return true;
        }
        throw new BaseException(BaseResponseStatus.TOO_MANY_REQUEST_ERROR);
    }

    private String getClientIpFromRequest(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    private Bucket createBucket() {
        Refill refill = Refill.intervally(10, Duration.ofMillis(100)); // 0.1초당 10회 충전
        Bandwidth limit = Bandwidth.classic(10, refill); // 최대 10회 가능
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}

