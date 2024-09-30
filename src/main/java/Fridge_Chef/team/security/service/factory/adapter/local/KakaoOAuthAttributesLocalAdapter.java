package Fridge_Chef.team.security.service.factory.adapter.local;

import Fridge_Chef.team.security.service.dto.OAuthAttributes;
import Fridge_Chef.team.security.service.factory.OAuthAttributesAdapter;
import Fridge_Chef.team.security.service.factory.adapter.KakaoOauthAttribute;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile({"local"})
public class KakaoOAuthAttributesLocalAdapter implements OAuthAttributesAdapter, KakaoOauthAttribute {
    private static final String REGISTRATION_ID = "kakao";

    @Override
    public OAuthAttributes toOAuthAttributes(Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        String email = (String) account.get("email");
        String name = (String) properties.get("nickname");
        String picture = (String) properties.get("profile_image");

        properties.put("id", attributes.get("id"));

        return OAuthAttributes.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .attributes(properties)
                .nameAttributeKey("nickname")
                .registrationId(REGISTRATION_ID)
                .build();
    }

    public ClientRegistration getKakaoClientRegistration() {
        return ClientRegistration.withRegistrationId(REGISTRATION_ID)
                .clientName("Kakao")
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .build();
    }

    @Override
    public boolean supports(String registrationId) {
        return REGISTRATION_ID.equals(registrationId);
    }
}
