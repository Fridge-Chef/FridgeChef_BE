package Fridge_Chef.team.common.auth;

import Fridge_Chef.team.user.domain.Role;
import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithAdminDetailsSecurityContextFactory.class)
public @interface WithMockCustomAdmin {

    String userId() default "default-user-id";
    String email() default "test@gmail.com";
    String username() default "testName";
    Role role() default Role.ADMIN;
}