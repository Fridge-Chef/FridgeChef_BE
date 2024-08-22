package Fridge_Chef.team.config.webhttps;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
@Profile("prod")
public class ProdConfig implements WebMvcConfigurer {

    @Value("${ssl.domain}")
    private String domain;
    @Value("${ssl.ip}")
    private String ip;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] ipOrigins = ip.split(",");
        String[] allowedOrigins = new String[ipOrigins.length + 1];
        System.arraycopy(ipOrigins, 0, allowedOrigins, 0, ipOrigins.length);
        allowedOrigins[ipOrigins.length] = domain;
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "DELETE","PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);  // 자격 증명 허용
    }
}
