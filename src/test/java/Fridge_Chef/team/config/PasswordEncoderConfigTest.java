package Fridge_Chef.team.config;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;

@Import(PasswordEncoderConfig.class)
public class PasswordEncoderConfigTest {

    @Test
    void passwordEncoderBeanIsCreated() {
        PasswordEncoderConfig config = new PasswordEncoderConfig();
        BCryptPasswordEncoder encoder = config.passwordEncoder();
        assertThat(encoder).isNotNull();
    }
}
