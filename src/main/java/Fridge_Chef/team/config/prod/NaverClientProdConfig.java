package Fridge_Chef.team.config.prod;


import Fridge_Chef.team.config.NaverClientConfig;
import org.springframework.beans.factory.annotation.Value;
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

@Profile({"dev","prod"})
@Configuration
public class NaverClientProdConfig implements NaverClientConfig {

    @Value("${client.naver.uri}")
    private String uri;

    @Value("${client.naver.client-id}")
    private String clientId;

    @Value("${client.naver.client-secret}")
    private String clientSecret;

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
