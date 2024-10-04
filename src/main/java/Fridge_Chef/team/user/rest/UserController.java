package Fridge_Chef.team.user.rest;

import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import Fridge_Chef.team.user.rest.request.UserAccountDeleteRequest;
import Fridge_Chef.team.user.rest.request.UserProfileImageUpdateRequest;
import Fridge_Chef.team.user.rest.request.UserProfileNameUpdateRequest;
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
    private final ImageService imageService;

    @GetMapping
    public UserProfileResponse get(@AuthenticationPrincipal AuthenticatedUser authenticatedUser) {
        User user = userService.findByUser(authenticatedUser.userId());
        return UserProfileResponse.from(user);
    }

    @PatchMapping("/name")
    public void profileNameUpdate(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody UserProfileNameUpdateRequest request){
        userService.updateUserProfileUsername(authenticatedUser.userId(),request);
    }

    @PatchMapping("/picture")
    public void profileImageUpdate(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody UserProfileImageUpdateRequest request){
        Image image = imageService.imageUpload(authenticatedUser.userId(),request.picture());
        userService.updateUserProfilePicture(authenticatedUser.userId(),image);
    }

    @DeleteMapping("/account")
    public void userAccount(@AuthenticationPrincipal AuthenticatedUser authenticatedUser, @RequestBody UserAccountDeleteRequest request) {
        userService.accountDelete(authenticatedUser.userId(), request.username());
    }
}