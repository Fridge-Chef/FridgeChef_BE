package Fridge_Chef.team.security.prod;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@Profile({"prod", "dev"})
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
        log.info("public key value : "+rsaPublicKey.getPublicExponent());

        log.info("Private Key Size: {}", rsaPrivateKey.getModulus().bitLength());
        log.info("Public Key Size: {}", rsaPublicKey.getModulus().bitLength());
    }

    public String create(UserId userId, Role role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusMinutes(EXPIRATION_MINUTES);

        log.info("Creating JWT for user: {}, role: {}", userId.getValue(), role);

        return Jwts.builder()
                .signWith(rsaPrivateKey, Jwts.SIG.RS256)
                .claim(TOKEN_USER_ID_PAYLOAD_PARAMETER, userId.getValue())
                .claim(TOKEN_USER_ROLE_PAYLOAD_PARAMETER, role)
                .issuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
                .expiration(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
                .compact();
    }


    public String createRefreshToken(UserId userId, Role role) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusDays(refreshTime);

        return Jwts.builder()
                .signWith(rsaPrivateKey, Jwts.SIG.RS256)
                .claim(TOKEN_USER_ID_PAYLOAD_PARAMETER, userId.getValue())
                .claim(TOKEN_USER_ROLE_PAYLOAD_PARAMETER, role.name())
                .issuedAt(Date.from(now.toInstant(ZoneOffset.UTC)))
                .expiration(Date.from(expirationTime.toInstant(ZoneOffset.UTC)))
                .compact();
    }

    public AuthenticatedUser parse(String jws) {
        log.info("JWT parse : " + jws);
        Claims claims = Jwts.parser()
                .verifyWith(rsaPublicKey)
                .build()
                .parseSignedClaims(jws)
                .getPayload();

        String userIdClaim = claims.get(TOKEN_USER_ID_PAYLOAD_PARAMETER, String.class);
        String roleClaim = claims.get(TOKEN_USER_ROLE_PAYLOAD_PARAMETER, String.class);

        log.info("JWT claim "+ userIdClaim+","+roleClaim);
        if (userIdClaim == null || roleClaim == null) {
            throw new ApiException(ErrorCode.TOKEN_ACCESS_EXPIRED_FAIL);
        }

        return new AuthenticatedUser(new UserId(UUID.fromString(userIdClaim)), Role.of(roleClaim));
    }
}
