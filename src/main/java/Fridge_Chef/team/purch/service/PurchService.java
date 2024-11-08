package Fridge_Chef.team.purch.service;

import Fridge_Chef.team.purch.coupang.CouPangManager;
import Fridge_Chef.team.purch.naver.NaverManager;
import Fridge_Chef.team.purch.service.response.PurchResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchService {
    private final NaverManager naverManager;
    private final CouPangManager couPangManager;

    public PurchResponse searchToShopLink(String search){
        return coupangSearchLink(search)
                .orElse(naverShopSearchLink(search));
    }

    private Optional<PurchResponse> coupangSearchLink(String search) {
        return couPangManager.search(search);
    }

    private PurchResponse naverShopSearchLink(String search) {
        return naverManager.search(search)
                .orElse(new PurchResponse("",""));
    }
}
