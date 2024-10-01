package Fridge_Chef.team.purch.service;

import Fridge_Chef.team.purch.coupang.CouPangManager;
import Fridge_Chef.team.purch.naver.NaverManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PurchService {
    private final NaverManager naverManager;
    private final CouPangManager couPangManager;

    public String searchToShopLink(String search){
        return coupangSearchLink(search)
                .orElse(naverShopSearchLink(search));
    }

    private Optional<String> coupangSearchLink(String search) {
        return couPangManager.search(search);
    }

    private String naverShopSearchLink(String search) {
        return naverManager.search(search)
                .orElse("");
    }
}
