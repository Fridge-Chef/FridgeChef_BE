package Fridge_Chef.team.security.service.factory.adapter;

import Fridge_Chef.team.security.service.dto.OAuthAttributes;
import Fridge_Chef.team.security.service.factory.OAuthAttributesAdapter;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class KakaoOAuthAttributesAdapter implements OAuthAttributesAdapter {
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

    @Override
    public boolean supports(String registrationId) {
        return REGISTRATION_ID.equals(registrationId);
    }
}
