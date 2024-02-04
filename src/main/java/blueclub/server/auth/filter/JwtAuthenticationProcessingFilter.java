package blueclub.server.auth.filter;

import blueclub.server.auth.domain.RefreshToken;
import blueclub.server.auth.repository.RefreshTokenRepository;
import blueclub.server.auth.service.JwtService;
import blueclub.server.global.response.BaseException;
import blueclub.server.global.response.BaseResponse;
import blueclub.server.global.response.BaseResponseStatus;
import blueclub.server.user.domain.User;
import blueclub.server.user.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 헤더에 AccessToken만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken을 요청 헤더에 AccessToken과 함께 요청
 *
 * 1. RefreshToken이 없고, AccessToken이 유효한 경우 -> 인증 성공 처리, RefreshToken을 재발급하지는 않는다.
 * 2. RefreshToken이 없고, AccessToken이 없거나 유효하지 않은 경우 -> 인증 실패 처리, 403 ERROR
 * 3. RefreshToken이 있는 경우 -> DB의 RefreshToken과 비교하여 일치하면 AccessToken 재발급, RefreshToken 재발급(RTR 방식)
 *                              인증 성공 처리는 하지 않고 실패 처리
 *
 */
@RequiredArgsConstructor
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    private static final List<String> NO_CHECK_URL_LIST = List.of(
            "/auth", "/health", "/css", "/images", "/js", "/favicon.ico", "/swagger", "/docs", "/swagger-ui", "/v3/api-docs", "/error"); // Filter 작동 X

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        for (String NO_CHECK_URL: NO_CHECK_URL_LIST) {
            if (request.getRequestURI().contains(NO_CHECK_URL)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        // RefreshToken 유효성 검사
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .filter(jwtService::isRefreshTokenExist)
                .orElse(null);

        // AccessToken 인증 시 다음 필터 진행
        if (checkAccessTokenAndAuthentication(request, response, filterChain)) {
            filterChain.doFilter(request, response);
            return;
        }

        // RefreshToken 없을 시 or 유효하지 않을 시 401 error
        if (refreshToken == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.setCharacterEncoding("utf-8");

            ObjectMapper objectMapper = new ObjectMapper();

            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            BaseResponse.builder()
                                    .code(BaseResponseStatus.INVALID_JWT.getCode())
                                    .message(BaseResponseStatus.INVALID_JWT.getMessage())
                                    .result(null)
                                    .build()
                    )
            );
            return;
        }

        // AccessToken 인증 실패 시 재발급
        checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
    }

    /**
     *  [리프레시 토큰으로 유저 정보 찾기 & 액세스 토큰/리프레시 토큰 재발급 메소드]
     *  파라미터로 들어온 헤더에서 추출한 리프레시 토큰으로 DB에서 유저를 찾고, 해당 유저가 있다면
     *  JwtService.createAccessToken()으로 AccessToken 생성,
     *  reIssueRefreshToken()로 리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드 호출
     *  그 후 JwtService.sendAccessTokenAndRefreshToken()으로 응답 헤더에 보내기
     */
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> {
                    User user = userRepository.findById(token.getId())
                            .orElseThrow(() -> new BaseException(BaseResponseStatus.MEMBER_NOT_FOUND_ERROR));
                    String reIssuedRefreshToken = reIssueRefreshToken(token);
                    try {
                        jwtService.sendAccessAndRefreshToken(
                                response,
                                jwtService.createAccessToken(user.getEmail()),
                                reIssuedRefreshToken
                        );
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * [리프레시 토큰 재발급 & DB에 리프레시 토큰 업데이트 메소드]
     * jwtService.createRefreshToken()으로 리프레시 토큰 재발급 후
     * DB에 재발급한 리프레시 토큰 업데이트 후 Flush
     */
    private String reIssueRefreshToken(RefreshToken token) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        token.updateRefreshToken(reIssuedRefreshToken);
        refreshTokenRepository.save(token);
        return reIssuedRefreshToken;
    }

    /**
     * [액세스 토큰 체크 & 인증 처리 메소드]
     * request에서 extractAccessToken()으로 액세스 토큰 추출 후, isTokenValid()로 유효한 토큰인지 검증
     * 유효한 토큰이면, 액세스 토큰에서 extractEmail로 Email을 추출한 후 findByEmail()로 해당 이메일을 사용하는 유저 객체 반환
     * 그 유저 객체를 saveAuthentication()으로 인증 처리하여
     * 인증 허가 처리된 객체를 SecurityContextHolder에 담기
     * 그 후 다음 인증 필터로 진행
     */
    public Boolean checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        AtomicBoolean isAuthSuccess = new AtomicBoolean(false);
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
                        .ifPresent(email -> {
                            Optional<User> user = userRepository.findByEmail(email);
                            if (user.isPresent()) {
                                saveAuthentication(user.get());
                                isAuthSuccess.set(true);
                            }
                        }));
        return isAuthSuccess.get();
    }

    /**
     * [인증 허가 메소드]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetails의 User 객체
     *
     * new UsernamePasswordAuthenticationToken()로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken의 파라미터
     * 1. 위에서 만든 UserDetailsUser 객체 (유저 정보)
     * 2. credential(보통 비밀번호로, 인증 시에는 보통 null로 제거)
     * 3. Collection < ? extends GrantedAuthority>로,
     * UserDetails의 User 객체 안에 Set<GrantedAuthority> authorities이 있어서 getter로 호출한 후에,
     * new NullAuthoritiesMapper()로 GrantedAuthoritiesMapper 객체를 생성하고 mapAuthorities()에 담기
     *
     * SecurityContextHolder.getContext()로 SecurityContext를 꺼낸 후,
     * setAuthentication()을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     */
    public void saveAuthentication(User myUser) {
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(RandomStringUtils.random(10)) // 임시 비밀번호
                .roles(myUser.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
