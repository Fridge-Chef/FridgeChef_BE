package Fridge_Chef.team.security;

import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class CustomJwtAuthenticationToken extends AbstractAuthenticationToken {

    private final AuthenticatedUser authenticatedUser;

    public CustomJwtAuthenticationToken(AuthenticatedUser authenticatedUser, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.authenticatedUser = authenticatedUser;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return authenticatedUser;
    }
}