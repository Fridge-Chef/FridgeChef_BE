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


    private static final Pattern pattern = Pattern.compile("([^,\\(\\n]+)\\s*\\(([^\\)]+)\\)");


    public List<RecipeIngredient> extractIngredients(String ingredients) {
        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        String[] lines = ingredients.split("\n");

        for (String line : lines) {
            if (line.contains(":")) {
                line = line.split(":")[1].trim();
            }

            String[] items = line.split(",");
            for (String item : items) {
                Matcher matcher = pattern.matcher(item.trim());

                if (matcher.find()) {
                    String ingredientName = matcher.group(1).trim();
                    String quantity = matcher.group(2).trim();
                    recipeIngredients.add(createRecipeIngredient(ingredientName, quantity));
                } else {
                    recipeIngredients.add(createRecipeIngredient(item.trim(), "X"));
                }
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
                .quantity(quantity.isEmpty() ? "X" : quantity)
                .build();
    }
}
