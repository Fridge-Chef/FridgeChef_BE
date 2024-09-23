package Fridge_Chef.team.user.rest.request;

import lombok.Data;

@Data
public class UserUpdateRequest {
    private String username;
    private String email;
}
