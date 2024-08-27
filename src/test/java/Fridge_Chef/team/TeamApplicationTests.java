package Fridge_Chef.team;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TeamApplicationTests {

    @Value("${jasypt.encryptor.password}")
    private String password;
    @Test
    void contextLoads() {
        System.out.println("password: "+password);

    }

}
