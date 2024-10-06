package Fridge_Chef.team.security.rest.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileLoginRequest {
    private String token;
    private String registration;
}
