package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.cert.service.CertService;
import Fridge_Chef.team.mail.rest.request.SignUpRequest;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import Fridge_Chef.team.user.rest.request.UserAccountDeleteRequest;
import Fridge_Chef.team.user.rest.request.UserAuthenticateRequest;
import Fridge_Chef.team.user.rest.request.UserPasswordChangeRequest;
import Fridge_Chef.team.user.rest.response.UserProfileResponse;
import Fridge_Chef.team.user.rest.response.UserResponse;
import Fridge_Chef.team.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtProvider jwtProvider;
    private final CertService certService;

    @GetMapping
    public UserProfileResponse get(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        User user = userService.findByUser(authenticatedUser.userId());
        return UserProfileResponse.from(user);
    }

    @PostMapping("/signup")
    public UserResponse signup(@Valid @RequestBody SignUpRequest request) {
        certService.validateCert(request.email());
        userService.validateMemberRegistration(request.email());
        User user = userService.signup(request.email(), request.password(), request.username());
        certService.deleteAuthenticationComplete(request);
        return createUserResponse(user);
    }

    @PostMapping("/login")
    public UserResponse authenticate(@RequestBody UserAuthenticateRequest request) {
        User user = userService.findUserByEmail(request.email());
        userService.authenticate(user, request.password());
        return createUserResponse(user);
    }

    @DeleteMapping("/account")
    public void userAccount(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody UserAccountDeleteRequest request) {
        userService.accountDelete(authenticatedUser.userId(), request.username());
    }

    @PatchMapping("/password")
    public void passwordChange(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody UserPasswordChangeRequest request) {
        userService.updatePassword(authenticatedUser.userId(), request.password(), request.newPassword());
    }

    private UserResponse createUserResponse(User user) {
        String token = jwtProvider.create(user.getUserId(), user.getRole());
        return UserResponse.from(user, token);
    }
}