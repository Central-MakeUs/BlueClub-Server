package blueclub.server.auth.config;

import blueclub.server.global.config.RateLimitingInterceptor;
import blueclub.server.user.service.UserFindService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserFindService userFindService;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://blueclubserver.shop", "https://www.blueclubserver.shop", "http://localhost:8080")
                .allowedMethods("GET", "POST", "DELETE", "PATCH", "PUT", "OPTIONS")
                .maxAge(3000);
    }

    /*
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitingInterceptor(userFindService));
    }

     */
}
