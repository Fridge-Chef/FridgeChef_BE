package Fridge_Chef.team.security.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.fridge.repository.FridgeRepository;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.security.service.dto.GoogleTokenDTO;
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
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private static final String GOOGLE_GET_TOKEN_URI = "https://oauth2.googleapis.com/tokeninfo?id_token";
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final UserHistoryRepository userHistoryRepository;
    private final FridgeRepository fridgeRepository;

    private final OAuthAttributesAdapterFactory oAuthAttributesAdapterFactory;
    private final DefaultOAuth2UserService defaultOAuth2UserService = new DefaultOAuth2UserService();
    private final RestTemplate restTemplate = new RestTemplate();


    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = defaultOAuth2UserService.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
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

    public User loadMoblieToUser(OAuth2UserRequest userRequest) {
        return saveOrUpdate(loadMoblie(userRequest));
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

    private User saveOrUpdate(OAuthAttributes attributes) {
        userLog(attributes, " 로그인 시도 ");
        Social loginType = Social.valueOf(attributes.registrationId().toUpperCase());

        User user = userRepository.findByProfileEmailAndProfileSocial(attributes.email(), loginType)
                .orElseGet(() -> registerNewUser(attributes, loginType));

        withdrawalAccountRecovery(user);

        return user;
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
            User signup = userRepository.save(user);
            fridgeRepository.save(Fridge.setup(signup));
            return signup;
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

    private void withdrawalAccountRecovery(User user) {
        if (user.getDeleteStatus() != null && !user.getDeleteStatus().bool()) {
            user.accountDelete(false);
        }
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

    private void userLog(OAuthAttributes attributes, String message) {
        log.info(attributes.registrationId() + attributes.email() + " : " + message);
    }
}