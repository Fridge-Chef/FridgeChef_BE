package Fridge_Chef.team.ingredient.repository;

import Fridge_Chef.team.ingredient.domain.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String ingredientName);
}
