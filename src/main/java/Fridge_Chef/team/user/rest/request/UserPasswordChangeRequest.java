package Fridge_Chef.team.user.rest.request;

import Fridge_Chef.team.common.validator.PasswordValid;

public record UserPasswordChangeRequest(
        @PasswordValid
        String password,
        @PasswordValid
        String newPassword) {
}
