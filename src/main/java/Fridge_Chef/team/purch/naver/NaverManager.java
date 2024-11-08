package Fridge_Chef.team.purch.naver;


import Fridge_Chef.team.config.NaverClientConfig;
import Fridge_Chef.team.purch.service.response.PurchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class NaverManager {
    private final NaverClientConfig config;

    public Optional<PurchResponse> search(String search) {
        return Optional.of(getNaverSearchLink(search));
    }

    @Retryable(backoff = @Backoff)
    public PurchResponse getNaverSearchLink(String search) {
        try {
            log.info("naver search...");
            String response = config.restClient().get()
                    .uri("/v1/search/shop?query=" + search + "&display=1&start=1&sort=sim")
                    .retrieve()
                    .body(NaverSearchResponse.class)
                    .firstItemLink();
            return new PurchResponse("naver",response);
        } catch (Exception e) {
            log.error("search error" + e);
            return null;
        }
    }
}
