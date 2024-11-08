package Fridge_Chef.team.purch.naver;


import Fridge_Chef.team.purch.service.response.PurchResponse;
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


    public Optional<PurchResponse> search(String search) {
        return Optional.of(getNaverSearchLink(search));
    }

    @Retryable(backoff = @Backoff)
    public PurchResponse getNaverSearchLink(String path) {
        try {
            NaverSearchResponse response = config.get()
                    .uri(path)
                    .retrieve()
                    .body(NaverSearchResponse.class);
            return new PurchResponse("naver",response.firstItemLink());
        } catch (Exception e) {
            return new PurchResponse("","");
        }
    }
}
