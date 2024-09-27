package Fridge_Chef.team.ingredient.repository;

import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient,Long> {
}
