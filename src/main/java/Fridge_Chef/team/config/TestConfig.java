package Fridge_Chef.team.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class TestConfig implements ApplicationRunner {

    private final Environment environment;

    public TestConfig(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("Active profiles: " + String.join(", ", environment.getActiveProfiles()));
    }
}