package Fridge_Chef.team.fridge.repository;

import Fridge_Chef.team.fridge.domain.FridgeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FridgeIngredientRepository extends JpaRepository<FridgeIngredient, Long> {
}
