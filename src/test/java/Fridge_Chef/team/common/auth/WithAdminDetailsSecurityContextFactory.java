package Fridge_Chef.team.common.auth;

import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.util.List;

public class WithAdminDetailsSecurityContextFactory implements WithSecurityContextFactory<WithMockCustomUser> {
    @Override
    public SecurityContext createSecurityContext(WithMockCustomUser user) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        AuthenticatedUser principal = new AuthenticatedUser(UserId.create(), user.role());

        Authentication auth =
                new UsernamePasswordAuthenticationToken(principal,null,List.of(user.role()));
        context.setAuthentication(auth);

        return context;
    }
}

