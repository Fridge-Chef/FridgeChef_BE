package Fridge_Chef.team.config.prod;


import Fridge_Chef.team.security.CustomJwtAuthenticationConverter;
import Fridge_Chef.team.security.handler.OAuth2SuccessHandler;
import Fridge_Chef.team.security.service.CustomOAuth2UserService;
import Fridge_Chef.team.user.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.interfaces.RSAPublicKey;

@Profile({"prod"})
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2SuccessHandler oAuth2AuthenticationSuccessHandler;
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
                .logout(logoutConfigurer -> logoutConfigurer.logoutSuccessUrl("/"))
                .oauth2ResourceServer(this::configureJwt)
                .oauth2Login(this::configureOAuth2Login)
                .build();
    }

    private void configureOAuth2Login(OAuth2LoginConfigurer<HttpSecurity> configurer) {
        configurer.userInfoEndpoint(endpointCustomizer -> endpointCustomizer.userService(customOAuth2UserService));
        configurer.successHandler(oAuth2AuthenticationSuccessHandler);
    }

    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry
                .requestMatchers("/", "/login","/login/oauth2/login/**","/static/**","/login/oauth2/code",
                        "/docs.html", "/favicon.ico", "/api/auth/**", "/api/cert/email/**",
                        "/api/email/**", "/api/user/signup", "/api/user/login",
                        "/api/ingredients/**", "/api/fridge/ingredients", "/api/recipes/", "/api/recipes/{id}",
                        "/api/boards","/api/boards/**","/api/mobile/auth/**","/api/mobile/auth/login"
                ).permitAll()
                .requestMatchers("/api/user", "/api/user/account",
                        "/api/categorys/{category_id}/board", "/api/recipes/{recipe_id}/comment","/api/books"

                )
                .hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority())
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