package Fridge_Chef.team.config.webhttps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Profile("dev")
public class DevConfig implements WebMvcConfigurer {

    @Value("${ssl.domain}")
    private String domain;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(domain)  // 모든 도메인 허용  ex) .allowedOrigins("http://192.168.0.1", "http://example.com", "http://another-domain.com")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")  // 모든 HTTP 메서드 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true);  // 자격 증명 허용
    }
}
