package Fridge_Chef.team.security.config;


import Fridge_Chef.team.security.SkipPathRequestMatcher;
import Fridge_Chef.team.security.filter.JwtTokenAuthenticationFilter;
import Fridge_Chef.team.security.filter.JwtTokenIssueFilter;
import Fridge_Chef.team.security.provider.JwtAuthenticationProvider;
import Fridge_Chef.team.security.provider.JwtTokenIssueProvider;
import Fridge_Chef.team.user.domain.Role;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    public static final String AUTHENTICATION_URL = "/api/auth/login";
    public static final String API_ROOT_URL = "/api/**";

    private final AuthenticationSuccessHandler successHandler;
    private final AuthenticationFailureHandler failureHandler;

    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(security -> security.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(this::authorizeHttpRequests)
                .addFilterBefore(jwtTokenIssueFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenAuthenticationFilter(List.of(AUTHENTICATION_URL), authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .formLogin(login -> login.loginPage("/login").permitAll());
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, JwtTokenIssueProvider jwtTokenIssueProvider, JwtAuthenticationProvider jwtAuthenticationProvider) throws Exception {
        var authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(jwtAuthenticationProvider);
        authenticationManagerBuilder.authenticationProvider(jwtTokenIssueProvider);
        return authenticationManagerBuilder.build();
    }

    private void authorizeHttpRequests(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry configurer) {
        configurer
                .requestMatchers("/","/docs.html","/favicon.io").permitAll()
                .requestMatchers("/api","/api/test","/api/token","/api/token/**","/api/signup","/api/signup/**").permitAll()
                .requestMatchers("/api/user/**").hasAnyRole(Role.USER.name(),Role.ADMIN.name())
                .requestMatchers("/api/admin/**").hasAnyRole(Role.ADMIN.name())
                .requestMatchers("/all/api/token/**").permitAll()
        ;
    }

    private JwtTokenIssueFilter jwtTokenIssueFilter(AuthenticationManager authenticationManager) {
        var filter = new JwtTokenIssueFilter(AUTHENTICATION_URL, objectMapper, successHandler, failureHandler);
        filter.setAuthenticationManager(authenticationManager);

        return filter;
    }

    private JwtTokenAuthenticationFilter jwtTokenAuthenticationFilter(List<String> pathsToSkip, AuthenticationManager authenticationManager) {
        var matcher = new SkipPathRequestMatcher(pathsToSkip, API_ROOT_URL);
        var filter = new JwtTokenAuthenticationFilter(matcher, failureHandler);
        filter.setAuthenticationManager(authenticationManager);

        return filter;
    }
}
