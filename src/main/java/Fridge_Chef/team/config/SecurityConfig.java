package Fridge_Chef.team.config;


import Fridge_Chef.team.security.CustomJwtAuthenticationConverter;
import Fridge_Chef.team.user.domain.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${jwt.secret.public}")
    private RSAPublicKey publicKey;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(this::configureHeaders)
                .authorizeHttpRequests(this::configureAuthorization)
                .oauth2ResourceServer(this::configureJwt)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers(
                        "/docs.html", "/favicon.ico", "/api/auth/**", "/api/cert/email/**",
                        "/api/user/email/**","/api/user/signup","/api/user/login"
                ).permitAll()
                .requestMatchers("/api/user","/api/user/account","/api/user/password").hasAnyRole(Role.USER.name(),Role.ADMIN.name())
                .requestMatchers(PathRequest.toH2Console()).permitAll()
                .anyRequest().authenticated();
    }

    private void configureJwt(OAuth2ResourceServerConfigurer<HttpSecurity> configurer) {
        configurer.jwt(jwt -> {
            jwt.decoder(jwtDecoder());
            jwt.jwtAuthenticationConverter(new CustomJwtAuthenticationConverter());
        });
    }

    private void configureHeaders(HeadersConfigurer<HttpSecurity> headers) {
        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin);
    }
}