package Fridge_Chef.team.ingredient.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.domain.IngredientCategory;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.rest.response.IngredientSearchResponse;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
                .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName)));
    }

    public Ingredient getOrCreate(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
                .orElseGet(() -> ingredientRepository.save(new Ingredient(ingredientName)));
    }

    @Transactional(readOnly = true)
    public IngredientSearchResponse findAllIngredients(){
        return new IngredientSearchResponse(
                ingredientRepository.findAll().stream()
                        .map(Ingredient::getName)
                        .toList()
        );
    }

    @Transactional(readOnly = true)
    public IngredientSearchResponse searchIngredients(String keyword) {
        List<String> ingredientNames = ingredientRepository.findByNameContaining(keyword)
                .stream()
                .map(Ingredient::getName)
                .collect(Collectors.toList());

        if (ingredientNames.isEmpty()) {
            throw new ApiException(ErrorCode.INGREDIENT_NOT_FOUND);
        }

        return new IngredientSearchResponse(ingredientNames);
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

    public IngredientCategory getIngredientCategory(String category) {
        try {
            return IngredientCategory.valueOf(category);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INGREDIENT_CATEGORY_INVALID);
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
