package Fridge_Chef.team.user.rest.model;

import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;

import java.util.Optional;

public record AuthenticatedUser(UserId userId, Role role) {
    public static Optional<UserId> anonymousUser(AuthenticatedUser user){
        return user == null ? Optional.empty(): Optional.of(user.userId());
    }
}
