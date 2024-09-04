package Fridge_Chef.team.recipe.repository;

import Fridge_Chef.team.recipe.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
    Optional<Recipe> findByName(String recipeName);
}
