package Fridge_Chef.team.user.repository;

import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    Optional<UserHistory> findByUserId(User user);
}
