package Fridge_Chef.team.user.job;

import Fridge_Chef.team.common.entity.OracleBoolean;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class UserJob {
    private final UserRepository userRepository;

    public UserJob(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Transactional
    @Scheduled(cron = "0 0 2 * * ?")
    void userDeletePolicy() {
        List<User> users = userRepository.findByDeleteStatus(OracleBoolean.T);
        userRepository.deleteAll(users);
    }
}
