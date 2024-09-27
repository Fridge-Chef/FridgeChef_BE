package Fridge_Chef.team.ingredient.service;

import Fridge_Chef.team.common.entity.OracleBoolean;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
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

    public static final List<String> SEASONINGS = List.of("소금", "후추", "설탕", "식초", "간장", "고추장", "기름", "식용유", "가루", "올리고당");
    private static final Pattern PREFIX_PATTERN = Pattern.compile("^[\\s●]*[재료|주재료|]*\\s*");
    private static final Pattern PATTERN_WITH_PARENTHESES = Pattern.compile("([^(]+)\\(([^)]+)\\)");
    private static final Pattern PATTERN_WITHOUT_PARENTHESES = Pattern.compile("([^,\\n]+?)\\s*(\\d+\\s*[gml]*)?");

    public List<RecipeIngredient> extractIngredients(String ingredients) {

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        Matcher prefixMatcher = PREFIX_PATTERN.matcher(ingredients);
        String prefixRemovedIngredients = prefixMatcher.replaceAll("").trim();

        String[] lines = prefixRemovedIngredients.split("\n");
        for (String line : lines) {
            if (line.contains(":")) {
                line = line.split(":")[1].trim();
            }

            String[] items = splitByCommaNotInParentheses(line);
            for (String item : items) {
                item = item.trim();
                Matcher matcher = (item.contains("(") && item.contains(")"))
                        ? PATTERN_WITH_PARENTHESES.matcher(item)
                        : PATTERN_WITHOUT_PARENTHESES.matcher(item);

                if (matcher.find()) {
                    String ingredientName = matcher.group(1).trim();
                    String quantity = matcher.group(2) != null ? matcher.group(2).trim() : "X";
                    recipeIngredients.add(createRecipeIngredient(ingredientName, quantity));
                } else {
                    recipeIngredients.add(createRecipeIngredient(item, "X"));
                }
            }
        }

        return recipeIngredients;
    }

    public Ingredient findIngredientByName(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }

    public boolean exist(String ingredientName) {
        return ingredientRepository.findByName(ingredientName).isPresent();
    }

    public void insertIngredient(Ingredient ingredient) {
        if (ingredientRepository.findByName(ingredient.getName()).isEmpty()) {
            ingredientRepository.save(ingredient);
        }
    }

    private RecipeIngredient createRecipeIngredient(String ingredientName, String quantity) {

        OracleBoolean isSeasoning = OracleBoolean.of(isSeasoning(ingredientName));

        Ingredient ingredient = ingredientRepository.findByName(ingredientName)
                .orElse(Ingredient.builder().name(ingredientName).isSeasoning(isSeasoning).build());

        return RecipeIngredient.builder()
                .ingredient(ingredient)
                .quantity(quantity.isEmpty() ? "X" : quantity)
                .build();
    }

    private boolean isSeasoning(String ingredientName) {
        return SEASONINGS.stream().anyMatch(seasoning -> ingredientName.contains(seasoning));
    }

    private String[] splitByCommaNotInParentheses(String input) {
        List<String> parts = new ArrayList<>();
        int parenthesesLevel = 0;
        StringBuilder currentPart = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == '(') {
                parenthesesLevel++;
            } else if (c == ')') {
                parenthesesLevel--;
            } else if (c == ',' && parenthesesLevel == 0) {
                parts.add(currentPart.toString().trim());
                currentPart.setLength(0);
                continue;
            }
            currentPart.append(c);
        }
        parts.add(currentPart.toString().trim());
        return parts.toArray(new String[0]);
    }
}
