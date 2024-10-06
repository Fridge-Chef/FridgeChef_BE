package Fridge_Chef.team.security.rest;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.security.rest.request.MobileLoginRequest;
import Fridge_Chef.team.security.service.CustomOAuth2UserService;
import Fridge_Chef.team.security.service.factory.provider.CustomOAuth2ClientProvider;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import Fridge_Chef.team.user.rest.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mobile/auth")
@RequiredArgsConstructor
public class OauthController {
    private final CustomOAuth2UserService oAuth2UserService;
    private final CustomOAuth2ClientProvider customOAuth2ClientProvider;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;

    @PostMapping("/login")
    public UserResponse login(@RequestBody MobileLoginRequest request) {
        ClientRegistration client = customOAuth2ClientProvider.getClientProperties(request.getRegistration());
        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, request.getToken(), null, null);

        OAuth2UserRequest userRequest = new OAuth2UserRequest(client, accessToken);
        OAuth2User oAuth2User = oAuth2UserService.loadUser(userRequest);
        UserId userId = new UserId((String) oAuth2User.getAttributes().get("userId"));
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_ACCESS_NOT_USER));
        String jwtToken = jwtProvider.create(userId, user.getRole());
        return createUserResponse(user, jwtToken);
    }

    private UserResponse createUserResponse(User user, String jwtToken) {
        return UserResponse.from(user, jwtToken);
    }
}
