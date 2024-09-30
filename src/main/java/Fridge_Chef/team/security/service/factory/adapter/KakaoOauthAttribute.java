package Fridge_Chef.team.security.service.factory.adapter;


import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.Map;

public interface KakaoOauthAttribute {
    ClientRegistration getKakaoClientRegistration();

    boolean supports(String registrationId);
}
