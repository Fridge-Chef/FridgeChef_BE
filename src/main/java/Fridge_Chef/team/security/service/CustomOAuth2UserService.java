package Fridge_Chef.team.security.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.security.service.dto.GoogleTokenDTO;
import Fridge_Chef.team.security.service.dto.OAuthAttributes;
import Fridge_Chef.team.security.service.factory.OAuthAttributesAdapterFactory;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.service.UserSignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final String GOOGLE_GET_TOKEN_URI;
    private final UserSignService userSignService;
    private final OAuthAttributesAdapterFactory oAuthAttributesAdapterFactory;
    private final DefaultOAuth2UserService defaultOAuth2UserService;
    private final RestTemplate restTemplate;

    public CustomOAuth2UserService(UserSignService userSignService, OAuthAttributesAdapterFactory oAuthAttributesAdapterFactory) {
        this.GOOGLE_GET_TOKEN_URI = "https://oauth2.googleapis.com/tokeninfo?id_token";
        this.userSignService = userSignService;
        this.oAuthAttributesAdapterFactory = oAuthAttributesAdapterFactory;
        this.defaultOAuth2UserService = new DefaultOAuth2UserService();
        this.restTemplate = new RestTemplate();
    }

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        var attributes = oAuthAttributes(registrationId, oAuth2User);
        var user = userSignService.saveOrUpdate(attributes);

        Map<String, Object> modifiableAttributes = new HashMap<>(attributes.attributes());
        modifiableAttributes.put("userId", user.getUserId().getValue().toString());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRoleKey())),
                modifiableAttributes,
                attributes.nameAttributeKey()
        );
    }

    @Transactional
    public User loadMoblieToUser(OAuth2UserRequest userRequest) {
        return userSignService.saveOrUpdate(loadMoblie(userRequest));
    }

    private OAuthAttributes loadMoblie(OAuth2UserRequest userRequest) {
        if (userRequest.getClientRegistration().getRegistrationId().equals("kakao")) {
            OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
            String registrationId = userRequest.getClientRegistration().getRegistrationId();
            return oAuthAttributes(registrationId, oAuth2User);
        } else if (userRequest.getClientRegistration().getRegistrationId().equals("google")) {
            return getGoogleToken(userRequest);
        }
        throw new ApiException(ErrorCode.TOKEN_ACCESS_EXPIRED_FAIL);
    }

    private OAuthAttributes oAuthAttributes(String registrationId, OAuth2User oAuth2User) {
        return oAuthAttributesAdapterFactory.factory(registrationId)
                .toOAuthAttributes(oAuth2User.getAttributes());
    }

    private OAuthAttributes getGoogleToken(OAuth2UserRequest request) {
        try {
            ResponseEntity<GoogleTokenDTO> response = restTemplate.getForEntity(GOOGLE_GET_TOKEN_URI + "=" + request.getAccessToken(), GoogleTokenDTO.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return Objects.requireNonNull(response.getBody()).toOAuthAttributes();
            }
        } catch (Exception ignored) {
        }
        throw new ApiException(ErrorCode.TOKEN_ACCESS_EXPIRED_FAIL);
    }
}