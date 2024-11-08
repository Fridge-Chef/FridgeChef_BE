package Fridge_Chef.team.purch.service;

import Fridge_Chef.team.purch.coupang.CouPangManager;
import Fridge_Chef.team.purch.naver.NaverManager;
import Fridge_Chef.team.purch.service.response.PurchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PurchService {
    private final NaverManager naverManager;
    private final CouPangManager couPangManager;

    public PurchResponse searchToShopLink(String search){
//        return coupangSearchLink(search)
        PurchResponse response = naverShopSearchLink(search);
        log.info("search :" + search+", site :"+response.site()+" link:"+ response.like());
        return response;
    }

    private Optional<PurchResponse> coupangSearchLink(String search) {
        // TODO : 쿠팡 연동후 연결 searchToShopLink() <-> couPangManager.search(search);
        return Optional.empty();
    }

    private PurchResponse naverShopSearchLink(String search) {
        return naverManager.search(search)
                .orElse(new PurchResponse("",""));
    }
}
