package Fridge_Chef.team.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("")
                .allowedMethods("GET", "POST","DELETE","PUT","PETCH") // 허용할 HTTP method
                .allowCredentials(true)
                .allowedHeaders("*");
        ; // 쿠키 인증 요청 허용;
    }
}