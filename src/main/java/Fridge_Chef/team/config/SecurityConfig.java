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
                        "/api/user/email/**","/api/user/signup","/api/user/login",
                        "/api/ingredients/**","/api/fridge/ingredients","/api/recipes/","/api/recipes/{id}",
                        "/api/categorys","/api/categorys/boards/**","/api/recipes/{recipe_id}/comments",
                        "/api/categorys/{category_id}/boards/{board_id}/comments"

                ).permitAll()
                .requestMatchers(
                        "/api/user","/api/user/account","/api/user/password",
                        "/api/recipes/book","/api/categorys/{category_id}/board","/api/recipes/{recipe_id}/comment",
                        "/api/categorys/{category_id}/boards/{board_id}/comment"
                )
                .hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority())
                .requestMatchers("/api/manager/busines/ingredient")
                .hasAnyAuthority(Role.ADMIN.getAuthority())
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