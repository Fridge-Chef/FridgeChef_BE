package Fridge_Chef.team.security;

import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;

public interface JwtProvider {
    String create(UserId userId, Role role);

    String createRefreshToken(UserId userId, Role role);

    AuthenticatedUser parse(String jws);
}
