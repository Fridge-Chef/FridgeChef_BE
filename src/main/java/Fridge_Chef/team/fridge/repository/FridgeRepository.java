package Fridge_Chef.team.fridge.repository;

import Fridge_Chef.team.fridge.domain.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {

    Optional<Fridge> findByUserId(UUID userid);
}
