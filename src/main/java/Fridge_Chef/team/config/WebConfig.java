package Fridge_Chef.team.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "https://localhost:3000")
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH") // 허용할 HTTP method
                .allowCredentials(true)
                .allowedHeaders("*");
        ;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}