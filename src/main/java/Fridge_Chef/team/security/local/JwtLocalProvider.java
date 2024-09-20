package Fridge_Chef.team.security.local;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Profile("local")
@Component
public class JwtLocalProvider implements JwtProvider {

    private static final String TOKEN_USER_ID_PAYLOAD_PARAMETER = "userId";
    private static final String TOKEN_USER_ROLE_PAYLOAD_PARAMETER = "role";
    private static final long EXPIRATION_MINUTES = 30L;
    private static final long refreshTime = 365;

    private static final KeyPair keyPair = generateKeyPair();
    private static final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
    private static final RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();


    public JwtLocalProvider() {
    }

    public String create(UserId userId, Role role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(EXPIRATION_MINUTES);

        return Jwts.builder()
                .signWith(rsaPrivateKey)
                .claim(TOKEN_USER_ID_PAYLOAD_PARAMETER, userId.getValue())
                .claim(TOKEN_USER_ROLE_PAYLOAD_PARAMETER, role.name())
                .issuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
                .expiration(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
                .compact();
    }


    public String createRefreshToken(UserId userId, Role role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusDays(refreshTime);

        return Jwts.builder()
                .signWith(rsaPrivateKey)
                .claim(TOKEN_USER_ID_PAYLOAD_PARAMETER, userId.getValue())
                .claim(TOKEN_USER_ROLE_PAYLOAD_PARAMETER, role.name())
                .issuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
                .expiration(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
                .compact();
    }

    public AuthenticatedUser parse(String jws) {
        Claims claims = Jwts.parser()
                .verifyWith(rsaPublicKey)
                .build()
                .parseSignedClaims(jws)
                .getPayload();

        String userIdClaim = claims.get(TOKEN_USER_ID_PAYLOAD_PARAMETER, String.class);
        String roleClaim = claims.get(TOKEN_USER_ROLE_PAYLOAD_PARAMETER, String.class);

        if (userIdClaim == null || roleClaim == null) {
            throw new ApiException(ErrorCode.TOKEN_ACCESS_EXPIRED_FAIL);
        }

        return new AuthenticatedUser(new UserId(UUID.fromString(userIdClaim)), Role.of(roleClaim));
    }

    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA key pair", e);
        }
    }
}
