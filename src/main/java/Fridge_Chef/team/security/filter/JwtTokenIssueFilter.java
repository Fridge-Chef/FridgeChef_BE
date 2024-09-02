package Fridge_Chef.team.security.filter;


import Fridge_Chef.team.security.exception.AuthMethodNotSupportedException;
import Fridge_Chef.team.security.filter.request.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;

@Slf4j
public class JwtTokenIssueFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper objectMapper;

    public JwtTokenIssueFilter(String defaultFilterProcessesUrl, ObjectMapper objectMapper,
                               AuthenticationSuccessHandler authenticationSuccessHandler,
                               AuthenticationFailureHandler authenticationFailureHandler) {
        super(defaultFilterProcessesUrl);
        this.objectMapper = objectMapper;
        this.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        this.setAuthenticationFailureHandler(authenticationFailureHandler);
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException {

        log.info("JwtTokenIssueFilter-   attemptAuthentication");
        if (!isPostMethod(request)) {
            throw new AuthMethodNotSupportedException("Authentication method not supported");
        }

        var loginRequest = objectMapper.readValue(request.getReader(), LoginRequest.class);
        var token = UsernamePasswordAuthenticationToken.unauthenticated(loginRequest.username(), loginRequest.password());

        return this.getAuthenticationManager().authenticate(token);
    }

    private boolean isPostMethod(HttpServletRequest request) {
        return HttpMethod.POST.name().equals(request.getMethod());
    }
}
