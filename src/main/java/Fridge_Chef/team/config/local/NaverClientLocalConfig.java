package Fridge_Chef.team.config.local;

import Fridge_Chef.team.config.NaverClientConfig;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpHeaders.HOST;

@Profile("local")
@Configuration
public class NaverClientLocalConfig implements NaverClientConfig {

    private final String uri = "";
    private final String clientId = "";
    private final String clientSecret = "";

    @Bean
    public RestClient restClient() {
        final ClientHttpRequestFactory requestFactory = ClientHttpRequestFactories.get(ClientHttpRequestFactorySettings.DEFAULTS
                .withReadTimeout(Duration.ofSeconds(100))
                .withConnectTimeout(Duration.ofSeconds(100)));

        return RestClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.add(HOST, "openapi.naver.com");
                    httpHeaders.add(CONTENT_TYPE, "plain/text");
                    httpHeaders.add("X-Naver-Client-Id", clientId);
                    httpHeaders.add("X-Naver-Client-Secret", clientSecret);
                })
                .requestFactory(requestFactory)
                .build();
    }
}
