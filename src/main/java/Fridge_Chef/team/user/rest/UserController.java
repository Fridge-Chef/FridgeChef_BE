package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import Fridge_Chef.team.user.rest.request.UserAuthenticateRequest;
import Fridge_Chef.team.user.rest.request.UserRegistrationRequest;
import Fridge_Chef.team.user.rest.response.UserResponse;
import Fridge_Chef.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;


    @PostMapping
    public UserResponse registration(@RequestBody UserRegistrationRequest request) {
        User registeredUser = userService.registerUser(request);
        return createUserResponse(registeredUser);
    }

    @PostMapping("/login")
    public UserResponse authenticate(@RequestBody UserAuthenticateRequest request) {
        User user = userService.findUserByEmail(request.email());
        userService.authenticate(user, request.password());
        return createUserResponse(user);
    }

    @GetMapping
    public UserResponse get(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        User user = userService.findByUserId(authenticatedUser)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
        return createUserResponse(user);
    }

    private UserResponse createUserResponse(User user) {
        String token = jwtProvider.create(user.getUserId());
        return UserResponse.from(user, token);
    }
}