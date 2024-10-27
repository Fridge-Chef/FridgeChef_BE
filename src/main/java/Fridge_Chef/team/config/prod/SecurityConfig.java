package Fridge_Chef.team.config.prod;


import Fridge_Chef.team.security.CustomJwtAuthenticationConverter;
import Fridge_Chef.team.security.handler.OAuth2SuccessHandler;
import Fridge_Chef.team.security.service.CustomOAuth2UserService;
import Fridge_Chef.team.user.domain.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
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
@Slf4j
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
        log.info("jwt public value : "+publicKey.getPublicExponent());
        log.info("jwt public getAlgorithm : "+publicKey.getAlgorithm());
        log.info("jwt public getModulus : "+publicKey.getModulus());
        log.info("jwt public getParams : "+publicKey.getParams());
        return NimbusJwtDecoder.withPublicKey(publicKey)
                .build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        boardMatchers(registry);
        commentMatchers(registry);
        ingredientsMatchers(registry);
        userMatchers(registry);
        registry.requestMatchers("/", "/static/**", "/docs.html", "/favicon.ico")
                .permitAll()
                .anyRequest().authenticated();
    }

    private void boardMatchers(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(HttpMethod.GET, "/api/boards", "/api/boards/{board_id}")
                .permitAll()
                .requestMatchers( "/api/boards/{board_id}/hit", "/api/board", "/api/books/{board_id}")
                .hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority());
    }

    private void commentMatchers(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers(HttpMethod.GET, "/api/boards/{board_id}/comments")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/boards/{board_id}/comments/{comment_id}")
                .permitAll()
                .requestMatchers(HttpMethod.POST, "/api/boards/{board_id}/comments")
                .hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority())
                .requestMatchers(HttpMethod.PUT, "/api/boards/{board_id}/comments/{comment_id}")
                .hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority())
                .requestMatchers(HttpMethod.DELETE, "/api/boards/{board_id}/comments/{comment_id}")
                .hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority())
                .requestMatchers(HttpMethod.PATCH, "/api/boards/{board_id}/comments/{comment_id}/like")
                .hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority());
    }

    private void userMatchers(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/api/mobile/auth/login", "/api/auth/**", "/login", "/login/oauth2/login/**", "/login/oauth2/code")
                .permitAll()
                .requestMatchers("/api/user", "/api/user/**")
                .hasAnyAuthority(Role.USER.getAuthority(), Role.ADMIN.getAuthority());
    }

    private void ingredientsMatchers(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry) {
        registry.requestMatchers("/api/fridge/ingredients", "/api/ingredients/**", "/api/fridge/ingredients", "/api/recipes/", "/api/recipes/**")
                .permitAll();
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