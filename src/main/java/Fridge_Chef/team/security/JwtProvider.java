package Fridge_Chef.team.security;

import Fridge_Chef.team.user.domain.UserId;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private static final String USER_ID_PAYLOAD_PARAMETER = "userId";
    private static final long EXPIRATION_MINUTES = 30L;
    private static final long refreshTime = 365;

    private final RSAPrivateKey rsaPrivateKey;
    private final RSAPublicKey rsaPublicKey;


    public JwtProvider(
            @Value("${jwt.secret.private}") RSAPrivateKey rsaPrivateKey,
            @Value("${jwt.secret.public}") RSAPublicKey rsaPublicKey) {
        this.rsaPrivateKey = rsaPrivateKey;
        this.rsaPublicKey = rsaPublicKey;
    }

    public String create(UserId userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(EXPIRATION_MINUTES);

        return Jwts.builder()
                .signWith(rsaPrivateKey)
                .claim(USER_ID_PAYLOAD_PARAMETER, userId.getValue())
                .issuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
                .expiration(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
                .compact();
    }


    public String createRefreshToken(UserId userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusDays(refreshTime);

        return Jwts.builder()
                .signWith(rsaPrivateKey)
                .claim(USER_ID_PAYLOAD_PARAMETER, userId.getValue())
                .issuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
                .expiration(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
                .compact();
    }

    public UserId parse(String jws) {
        return getUserId(jws);
    }

    private UserId getUserId(String jws) {
        Claims claims = Jwts.parser()
                .verifyWith(rsaPublicKey)
                .build()
                .parseSignedClaims(jws)
                .getPayload();

        String userIdClaim = claims.get(JwtProvider.USER_ID_PAYLOAD_PARAMETER, String.class);
        if (userIdClaim == null) {
            throw new IllegalArgumentException("userId is required");
        }
        return new UserId(UUID.fromString(userIdClaim));
    }
}
