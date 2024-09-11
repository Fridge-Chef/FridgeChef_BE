package Fridge_Chef.team.security.rest;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.security.rest.response.TokenRefreshResponse;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import Fridge_Chef.team.user.rest.response.UserResponse;
import Fridge_Chef.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class TokenController {
    private final UserService userService;
    private final JwtProvider jwtProvider;


    @GetMapping("/access")
    public UserResponse accessLogin(@AuthenticationPrincipal AuthenticatedUser userId) {
        User user = userService.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_ACCESS_NOT_USER));
        return createUserResponse(user);
    }

    @GetMapping("/refresh")
    public TokenRefreshResponse isValidToken(@AuthenticationPrincipal AuthenticatedUser userId) {
        User user = userService.findByUserId(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.TOKEN_ACCESS_NOT_USER));
        return createUserRefreshTokenResponse(user);
    }

    private UserResponse createUserResponse(User user) {
        String token = jwtProvider.create(user.getUserId(),user.getRole());
        return UserResponse.from(user, token);
    }

    private TokenRefreshResponse createUserRefreshTokenResponse(User user) {
        String token = jwtProvider.createRefreshToken(user.getUserId(),user.getRole());
        return TokenRefreshResponse.from(token);
    }
}
