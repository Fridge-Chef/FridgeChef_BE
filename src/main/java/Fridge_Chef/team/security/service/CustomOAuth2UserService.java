package Fridge_Chef.team.security.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.security.service.dto.OAuthAttributes;
import Fridge_Chef.team.security.service.factory.OAuthAttributesAdapterFactory;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.Social;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;

    private final OAuthAttributesAdapterFactory oAuthAttributesAdapterFactory;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        var registrationId = userRequest.getClientRegistration().getRegistrationId();
        var attributes = oAuthAttributes(registrationId, oAuth2User);
        var user = saveOrUpdate(attributes);
        attributes.attributes().put("userId",user.getUserId().getValue().toString());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.attributes(),
                attributes.nameAttributeKey()
        );
    }

    private OAuthAttributes oAuthAttributes(String registrationId, OAuth2User oAuth2User) {
        return oAuthAttributesAdapterFactory.factory(registrationId)
                .toOAuthAttributes(oAuth2User.getAttributes());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        userLog(attributes, " 로그인 시도 ");

        return userRepository.findByEmail(attributes.email())
                .map(entity -> entity.update(attributes.name(), attributes.picture()))
                .orElseGet(() -> registerNewUser(attributes));
    }

    private User signup(OAuthAttributes attributes) {
        userLog(attributes, " 회원가입 ");

        User user = User.createSocialUser(attributes.email(),
                attributes.name(),
                Role.USER,
                Social.valueOf(attributes.registrationId().toUpperCase()));

        Image image = imageRepository.save(Image.outUri(attributes.picture()));
        user.updatePicture(image);
        return userRepository.save(user);
    }

    private User registerNewUser(OAuthAttributes attributes) {
        if (userRepository.existsByEmail(attributes.email())) {
            throw new ApiException(ErrorCode.USER_EMAIL_UNIQUE);
        }
        return signup(attributes);
    }

    private void userLog(OAuthAttributes attributes, String message) {
        log.info(attributes.registrationId() + attributes.email() + " : " + message);
    }
}
