package blueclub.server.auth.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
public class TestWebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://blueclubserver.shop", "https://www.blueclubserver.shop", "http://localhost:8080")
                .allowedMethods("GET", "POST", "DELETE", "PATCH", "PUT", "OPTIONS")
                .maxAge(3000);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new TestRateLimitingInterceptor());
    }
}

