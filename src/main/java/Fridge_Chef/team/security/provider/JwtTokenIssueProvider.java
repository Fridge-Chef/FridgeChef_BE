package Fridge_Chef.team.security.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenIssueProvider implements AuthenticationProvider {
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        var username = (String) authentication.getPrincipal();
        var password = (String) authentication.getCredentials();

        return authenticate(username, password);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        log.info("JwtTokenIssueProvider  -  supports");
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    private UsernamePasswordAuthenticationToken authenticate(String username, String password) {
        log.info("로그인 처리 provider - 인증 시도중 ... ");
        UserDetails user = userDetailsService.loadUserByUsername(username);
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("인증 실패. username or password 불일치");
        }
        log.info("로그인 처리 provider - ["+user.getUsername()+"] - 인증 성공");
        return UsernamePasswordAuthenticationToken.authenticated(user.getUsername(), null, authorities(user));
    }

    private List<SimpleGrantedAuthority> authorities(UserDetails user) {
        return user.getAuthorities().stream()
                .map(authority -> {
                    log.info("인증자 ["+user.getUsername() + "] 의 권한 부여 LEVEL: " + authority.getAuthority());
                    return new SimpleGrantedAuthority(authority.getAuthority());
                }).toList();
    }
}
