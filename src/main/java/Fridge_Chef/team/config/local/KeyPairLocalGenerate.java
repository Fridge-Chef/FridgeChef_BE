package Fridge_Chef.team.config.local;

import lombok.Getter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

@Getter
@Profile("local")
@Configuration
public class KeyPairLocalGenerate {
    private static final KeyPair keyPair = generateKeyPair();
    private static final RSAPrivateKey rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
    private static final RSAPublicKey rsaPublicKey = (RSAPublicKey) keyPair.getPublic();

    @Bean
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
