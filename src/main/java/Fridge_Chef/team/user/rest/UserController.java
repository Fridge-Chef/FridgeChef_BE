package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import Fridge_Chef.team.user.rest.request.UserAccountDeleteRequest;
import Fridge_Chef.team.user.rest.response.UserProfileResponse;
import Fridge_Chef.team.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public UserProfileResponse get(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        User user = userService.findByUser(authenticatedUser.userId());
        return UserProfileResponse.from(user);
    }

    @DeleteMapping("/account")
    public void userAccount(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody UserAccountDeleteRequest request) {
        userService.accountDelete(authenticatedUser.userId(), request.username());
    }
}