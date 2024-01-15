package blueclub.server.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://blueclubserver.shop")
                .allowedOrigins("http://localhost:8080")
                .allowedMethods("GET", "POST", "DELETE", "PATCH")
                .maxAge(3000);
    }
}
