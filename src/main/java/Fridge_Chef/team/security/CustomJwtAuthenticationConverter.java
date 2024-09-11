package Fridge_Chef.team.security;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String userIdClaim = (String) jwt.getClaims().get("userId");
        String roleClaim = (String) jwt.getClaims().get("role");
        if (userIdClaim == null) {
            throw new ApiException(ErrorCode.TOKEN_ACCESS_EXPIRED_FAIL);
        }
        Collection<GrantedAuthority> auth = List.of(Role.of(roleClaim));
        return new CustomJwtAuthenticationToken(
                new AuthenticatedUser(new UserId(UUID.fromString(userIdClaim)),
                        Role.of(roleClaim)), auth);
    }
}
