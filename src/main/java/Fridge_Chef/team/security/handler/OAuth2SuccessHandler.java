package Fridge_Chef.team.security.handler;

import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + toToken(oAuth2User));
    }

    private String toToken(OAuth2User user) {
        String userId = (String) user.getAttributes().get("userId");
        log.info("oauth Handler 토큰생성 : "+ userId);

        Role roles = user.getAuthorities()
                .stream()
                .map(authority -> Role.of(authority.getAuthority()))
                .findAny()
                .orElse(Role.USER);

        return jwtProvider.create(new UserId(userId), roles);
    }
}
