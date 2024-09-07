package Fridge_Chef.team.security;

import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.UUID;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        String userIdClaim = (String) jwt.getClaims().get("userId");
        if (userIdClaim == null) {
            throw new IllegalArgumentException("userId is required");
        }
        return new CustomJwtAuthenticationToken(
                new AuthenticatedUser(new UserId(UUID.fromString(userIdClaim))),
                new JwtAuthenticationConverter().convert(jwt).getAuthorities());
    }
}
