package Fridge_Chef.team.user.rest.model;

import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;
public record AuthenticatedUser(UserId userId, Role role) {
}
