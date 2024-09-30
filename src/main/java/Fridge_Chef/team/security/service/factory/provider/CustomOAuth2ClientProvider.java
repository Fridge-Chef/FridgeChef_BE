package Fridge_Chef.team.security.service.factory.provider;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.security.service.factory.adapter.GoogleOauthAttribute;
import Fridge_Chef.team.security.service.factory.adapter.KakaoOauthAttribute;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOAuth2ClientProvider {

    private final GoogleOauthAttribute googleProperties;
    private final KakaoOauthAttribute kakaoProperties;


    public ClientRegistration getClientProperties(String registrationId) {
        log.info("Moblie sns type valid.. " + registrationId);
        if (kakaoProperties.supports(registrationId)) {
            return kakaoProperties.getKakaoClientRegistration();
        }
        if (googleProperties.supports(registrationId)) {
            return googleProperties.getGoogleClientRegistration();
        }
        throw new ApiException(ErrorCode.SIGNUP_SNS_NOT_SUPPORT);
    }
}
