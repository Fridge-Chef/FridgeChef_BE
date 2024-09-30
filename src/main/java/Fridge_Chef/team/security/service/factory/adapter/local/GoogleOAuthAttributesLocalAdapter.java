package Fridge_Chef.team.security.service.factory.adapter.local;

import Fridge_Chef.team.security.service.dto.OAuthAttributes;
import Fridge_Chef.team.security.service.factory.OAuthAttributesAdapter;
import Fridge_Chef.team.security.service.factory.adapter.GoogleOauthAttribute;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Profile("local")
public class GoogleOAuthAttributesLocalAdapter implements OAuthAttributesAdapter, GoogleOauthAttribute {
    private static final String REGISTRATION_ID = "google";

    @Override
    public OAuthAttributes toOAuthAttributes(Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey("name")
                .registrationId(REGISTRATION_ID)
                .build();
    }

    @Override
    public ClientRegistration getGoogleClientRegistration() {
        return null;
    }

    @Override
    public boolean supports(String registrationId) {
        return REGISTRATION_ID.equals(registrationId);
    }
}