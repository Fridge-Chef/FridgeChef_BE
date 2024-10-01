package Fridge_Chef.team.purch.naver;


import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class NaverManager {
    private final RestClient config;


    public Optional<String> search(String search) {
        return Optional.of(getNaverSearchLink(search));
    }

    @Retryable(backoff = @Backoff)
    public String getNaverSearchLink(String path) {
        try {
            NaverSearchResponse response = config.get()
                    .uri(path)
                    .retrieve()
                    .body(NaverSearchResponse.class);
            return response.firstItemLink();
        } catch (Exception e) {
            return "";
        }
    }
}
