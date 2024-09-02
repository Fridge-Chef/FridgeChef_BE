package Fridge_Chef.team.security.rest.dto;

import Fridge_Chef.team.user.domain.Role;

import java.util.List;

public record JwtAccessTokenResponse(String username, List<Role> authentications) {

}
