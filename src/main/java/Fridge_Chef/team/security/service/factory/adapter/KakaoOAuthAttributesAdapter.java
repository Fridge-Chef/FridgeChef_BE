package Fridge_Chef.team.security.service.factory.adapter;

import Fridge_Chef.team.security.service.dto.OAuthAttributes;
import Fridge_Chef.team.security.service.factory.OAuthAttributesAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Profile({"dev","prod"})
public class KakaoOAuthAttributesAdapter implements OAuthAttributesAdapter ,KakaoOauthAttribute{
    private static final String REGISTRATION_ID = "kakao";

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    protected String clientId;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    protected String clientSecret;

    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    protected String redirectUri;

    @Value("${spring.security.oauth2.client.provider.kakao.authorization-uri}")
    protected String authorizationUri;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    protected String tokenUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    protected String userInfoUri;

    @Value("${spring.security.oauth2.client.provider.kakao.user-name-attribute}")
    protected String userNameAttribute;
    private List<String> scopes= List.of("profile_nickname","account_email","profile_image");

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
                .clientId(clientId)
                .clientSecret(clientSecret)
                .redirectUri(redirectUri)
                .scope(scopes)
                .authorizationUri(authorizationUri)
                .tokenUri(tokenUri)
                .userInfoUri(userInfoUri)
                .userNameAttributeName(userNameAttribute)
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
