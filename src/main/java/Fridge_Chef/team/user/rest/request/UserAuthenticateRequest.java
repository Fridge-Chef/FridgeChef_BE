package Fridge_Chef.team.user.rest.request;

import Fridge_Chef.team.common.validator.PasswordValid;
import jakarta.validation.constraints.Email;

public record UserAuthenticateRequest(

        @Email
        String email,
        @PasswordValid
        String password) {
}
