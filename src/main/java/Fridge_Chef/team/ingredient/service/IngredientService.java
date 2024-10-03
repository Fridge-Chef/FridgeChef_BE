package Fridge_Chef.team.ingredient.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    public Ingredient createIngredient(String ingredientName) {
        return Ingredient.builder()
                .name(ingredientName)
                .build();
    }

    public Optional<Ingredient> getIngredient(String ingredientName) {
        return ingredientRepository.findByName(ingredientName);
    }

    public boolean exist(String ingredientName) {
        return ingredientRepository.findByName(ingredientName).isPresent();
    }

    public Ingredient insertIngredient(Ingredient ingredient) {
        Optional<Ingredient> data = ingredientRepository.findByName(ingredient.getName());

        if (data.isPresent()) {
            return data.get(); // 이미 존재하는 경우 해당 Ingredient 반환
        } else {
            return ingredientRepository.save(ingredient); // 새로 저장한 Ingredient 반환
        }
    }

    private RecipeIngredient createRecipeIngredient(String ingredientName, String quantity) {

        if (ingredientName == null || ingredientName.trim().isEmpty()) {
            throw new ApiException(ErrorCode.INGREDIENT_NOT_FOUND);
        }

        Ingredient ingredient = ingredientRepository.findByName(ingredientName)
                .orElseGet(() -> Ingredient.builder().name(ingredientName).build());

        return RecipeIngredient.builder()
                .ingredient(ingredient)
                .quantity(quantity.isEmpty() ? "X" : quantity)
                .build();
    }
}
