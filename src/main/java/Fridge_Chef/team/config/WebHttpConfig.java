package Fridge_Chef.team.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
public class WebHttpConfig implements WebMvcConfigurer {

    @Value("${ssl.ip}")
    private String ip;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = ip.split(","); //ex) 127.0.0.1,127.0.0.2

        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")  // 모든 HTTP 메서드 허용
                .allowedHeaders("*")  // 모든 헤더 허용
                .allowCredentials(true);  // 자격 증명 허용
    }
}
