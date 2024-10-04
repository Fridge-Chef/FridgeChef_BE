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
import Fridge_Chef.team.user.domain.UserHistory;
import Fridge_Chef.team.user.repository.UserHistoryRepository;
import Fridge_Chef.team.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final UserHistoryRepository userHistoryRepository;


    private final OAuthAttributesAdapterFactory oAuthAttributesAdapterFactory;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        var oAuth2User = defaultOAuth2UserService.loadUser(userRequest);

        var registrationId = userRequest.getClientRegistration().getRegistrationId();
        var attributes = oAuthAttributes(registrationId, oAuth2User);
        var user = saveOrUpdate(attributes);

        Map<String, Object> modifiableAttributes = new HashMap<>(attributes.attributes());
        modifiableAttributes.put("userId", user.getUserId().getValue().toString());

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority(user.getRoleKey())),
                modifiableAttributes,
                attributes.nameAttributeKey()
        );
    }

    public OAuthAttributes oAuthAttributes(String registrationId, OAuth2User oAuth2User) {
        return oAuthAttributesAdapterFactory.factory(registrationId)
                .toOAuthAttributes(oAuth2User.getAttributes());
    }

    public User saveOrUpdate(OAuthAttributes attributes) {
        userLog(attributes, " 로그인 시도 ");
        Social loginType = Social.valueOf(attributes.registrationId().toUpperCase());

        User user = userRepository.findByProfileEmailAndProfileSocial(attributes.email(), loginType)
                .orElseGet(() -> registerNewUser(attributes, loginType));

        userPolicy(user);
        return user;
    }

    private void userPolicy(User user) {
        if (user.getHistory() == null) {
            userHistoryRepository.save(new UserHistory(user)).update();
        }
    }

    private User signup(OAuthAttributes attributes) {
        userLog(attributes, " 회원가입 ");
        Social social = Social.signupOf(attributes.registrationId().toUpperCase());
        User user = User.createSocialUser(
                attributes.email(),
                attributes.name(),
                Role.USER,
                social);

        Image image = imageRepository.save(Image.outUri(attributes.picture()));
        user.updatePicture(image);

        try {
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(ErrorCode.SIGNUP_USER_FAIL_SNS_EMAIL_UNIQUE);
        }
    }

    private User registerNewUser(OAuthAttributes attributes, Social social) {
        if (userRepository.existsByProfileEmailAndProfileSocial(attributes.email(), social)) {
            throw new ApiException(ErrorCode.SIGNUP_USER_FAIL_SNS_EMAIL_UNIQUE);
        }
        User user = signup(attributes);
        userHistoryRepository.save(new UserHistory(user));
        return user;
    }

    private void userLog(OAuthAttributes attributes, String message) {
        log.info(attributes.registrationId() + attributes.email() + " : " + message);
    }
}
