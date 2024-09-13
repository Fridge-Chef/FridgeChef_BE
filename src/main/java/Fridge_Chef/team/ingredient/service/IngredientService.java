package Fridge_Chef.team.ingredient.service;

import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;

    private static final Pattern pattern = Pattern.compile("(.*?)(?:\\(([^)]+)\\))?$");

    public List<RecipeIngredient> extractIngredients(String ingredients) {
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();
        String[] ingredientLines = ingredients.split("\\n");

        for (String line : ingredientLines) {
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.matches()) {
                String ingredientName = matcher.group(1).trim();
                String quantity = matcher.group(2) != null ? matcher.group(2).trim() : "알 수 없음";
                recipeIngredients.add(createRecipeIngredient(ingredientName, quantity));
            }
        }

        return recipeIngredients;
    }

    public void save(Ingredient ingredient) {
        if (ingredient.getId() == null || !ingredientRepository.existsById(ingredient.getId())) {
            ingredientRepository.save(ingredient);
        }
    }

    private RecipeIngredient createRecipeIngredient(String ingredientName, String quantity) {

        Ingredient ingredient = ingredientRepository.findByName(ingredientName)
                .orElse(Ingredient.builder().name(ingredientName).isSeasoning(false).build());

        return RecipeIngredient.builder()
                .ingredient(ingredient)
                .quantity(quantity.isEmpty() ? "알 수 없음" : quantity)
                .build();
    }
}
