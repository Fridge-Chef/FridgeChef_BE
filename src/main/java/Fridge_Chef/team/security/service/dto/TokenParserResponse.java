package Fridge_Chef.team.security.service.dto;


import Fridge_Chef.team.user.domain.Role;

import java.util.List;

public record TokenParserResponse(String username, List<Role> roles) {
}
