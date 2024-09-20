package Fridge_Chef.team.security.prod;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Profile({"prod", "dev"})
@Component
public class JwtProdProvider implements JwtProvider {

    private static final String TOKEN_USER_ID_PAYLOAD_PARAMETER = "userId";
    private static final String TOKEN_USER_ROLE_PAYLOAD_PARAMETER = "role";
    private static final long EXPIRATION_MINUTES = 30L;
    private static final long refreshTime = 365;

    private final RSAPrivateKey rsaPrivateKey;
    private final RSAPublicKey rsaPublicKey;


    public JwtProdProvider(
            @Value("${jwt.secret.private}") RSAPrivateKey rsaPrivateKey,
            @Value("${jwt.secret.public}") RSAPublicKey rsaPublicKey) {
        this.rsaPrivateKey = rsaPrivateKey;
        this.rsaPublicKey = rsaPublicKey;
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
}
