package Fridge_Chef.team.recipe.service;

import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.rest.request.RecipeCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecipeIngredientService {

    private final IngredientService ingredientService;

    private final RecipeIngredientRepository recipeIngredientRepository;

    @Transactional
    public List<RecipeIngredient> getOrCreate(RecipeCreateRequest request) {
        return request.getRecipeIngredients().stream()
                .map(this::findOrSaveIngredient)
                .collect(Collectors.toList());
    }

    private RecipeIngredient findOrSaveIngredient(RecipeCreateRequest.RecipeIngredient recipeIngredient) {

        Ingredient ingredient = ingredientService.getOrCreate(recipeIngredient.getName());
        RecipeIngredient ri = new RecipeIngredient(ingredient, recipeIngredient.getDetails());

        return recipeIngredientRepository.save(ri);
    }
}
