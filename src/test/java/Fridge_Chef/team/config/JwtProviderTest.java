package Fridge_Chef.team.config;

import Fridge_Chef.team.security.JwtProvider;
import Fridge_Chef.team.user.domain.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Configuration;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Configuration
@DisplayName("JwtProvider")
public class JwtProviderTest {
    private static final KeyPair keyPair = generateKeyPair();
    private static final RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
    private static final RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

    private static final UserId TEST_USER_ID = new UserId(UUID.randomUUID());
    private static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Failed to generate RSA key pair", e);
        }
    }

    @Test
    @DisplayName("jwt 생성 ")
    public void shouldCreateJWTTokenSuccessfully() {
        JwtProvider sut = new JwtProvider(privateKey, publicKey);
        assertDoesNotThrow(() -> sut.create(TEST_USER_ID));
    }

    @Test
    @DisplayName("jwt to String 변환 ")
    public void shouldParseJWTTokenCorrectly() {
        JwtProvider sut = new JwtProvider(privateKey, publicKey);
        String jws = sut.create(TEST_USER_ID);
        assertEquals(TEST_USER_ID, sut.parse(jws));
    }
}