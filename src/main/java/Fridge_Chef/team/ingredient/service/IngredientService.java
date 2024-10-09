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

    public Ingredient getIngredient(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
                .orElseThrow(() -> new ApiException(ErrorCode.INGREDIENT_NOT_FOUND));
    }

    public Ingredient getOrCreate(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName)));
    }

    public boolean exist(String ingredientName) {
        return ingredientRepository.findByName(ingredientName).isPresent();
    }

    public Ingredient insertIngredient(Ingredient ingredient) {
        Optional<Ingredient> data = ingredientRepository.findByName(ingredient.getName());

        if (data.isPresent()) {
            return data.get();
        } else {
            return ingredientRepository.save(ingredient);
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
