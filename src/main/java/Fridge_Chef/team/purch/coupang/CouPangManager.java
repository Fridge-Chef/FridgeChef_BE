package Fridge_Chef.team.purch.coupang;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CouPangManager {

    public Optional<String> search(String search) {
        // externalApiTest/java/fridge_chef/team/shop/ClickEventTest 참고
        /// error code 검출시 null 리턴
        return Optional.of(null);
    }
}
