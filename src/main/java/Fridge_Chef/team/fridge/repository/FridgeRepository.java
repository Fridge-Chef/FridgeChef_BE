package Fridge_Chef.team.fridge.repository;

import Fridge_Chef.team.fridge.domain.Fridge;
import Fridge_Chef.team.user.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {

    Optional<Fridge> findByUserId(UserId userid);
}
