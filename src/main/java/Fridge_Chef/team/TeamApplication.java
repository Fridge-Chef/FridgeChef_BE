package Fridge_Chef.team;

import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.service.UserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
@RequiredArgsConstructor
public class TeamApplication {
    private final UserService userService;

    public static void main(String[] args) {
        SpringApplication.run(TeamApplication.class, args);
    }

    @PostConstruct
    public void setUser() {
        User admin = userService.signup("admin@gmail.com", "adminadmin", "admin", Role.ADMIN);
        User user = userService.signup("user@gmail.com", "useruser", "user");
    }
}
