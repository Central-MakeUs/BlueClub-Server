package blueclub.server.global.config;

import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.domain.User;
import blueclub.server.user.service.UserFindService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
@RequiredArgsConstructor
public class RateLimitingInterceptor implements HandlerInterceptor {

    private final UserFindService userFindService;
    private final Map<String, Long> lastAccessMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = getUserIdFromRequest(request); // 요청에서 accountId를 추출하는 로직을 구현해야 합니다.
        synchronized (this) {
            long now = System.currentTimeMillis();
            long lastAccess = lastAccessMap.getOrDefault(userId, 0L);
            long elapsedTime = now - lastAccess;
            if (elapsedTime < 1000) { // 1초?당 1회로 제한
                Thread.sleep(1000 - elapsedTime);
            }
            lastAccessMap.put(userId, System.currentTimeMillis());
        }
        return true;
    }

    private String getUserIdFromRequest(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!authentication.isAuthenticated())
            throw new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND_ERROR);
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userFindService.findByUserDetails(userDetails);
        request.getRemoteHost();
        request.getRemoteAddr();
        request.getLocalAddr();
        return String.valueOf(user.getId());
    }
}


 */