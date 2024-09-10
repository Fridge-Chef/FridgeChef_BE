package Fridge_Chef.team.user.rest.request;

import Fridge_Chef.team.common.validator.PasswordValid;
import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
    @PasswordValid
    private String password;
}
