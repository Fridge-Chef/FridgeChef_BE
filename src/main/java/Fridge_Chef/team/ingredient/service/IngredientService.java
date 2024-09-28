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

    public static final List<String> SEASONINGS = List.of("소금", "후추", "설탕", "식초", "간장", "고추장", "기름", "식용유", "가루", "올리고당", "참깨", "액젓", "통깨", "매실액");
    private static final Pattern PREFIX_PATTERN = Pattern.compile("^[\\s●]*(?:재료|주재료)?\\s*"); // 재료 앞 불필요한 텍스트 제거
    private static final Pattern PATTERN_WITH_PARENTHESES = Pattern.compile("([^()]+)\\(([^)]+)\\)"); // 괄호 안에 수량이 있는 경우
    private static final Pattern PATTERN_WITHOUT_PARENTHESES = Pattern.compile("([^,\\n]+)"); // 괄호가 없는 경우

    public List<RecipeIngredient> extractIngredients(String ingredients) {

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        Matcher prefixMatcher = PREFIX_PATTERN.matcher(ingredients);
        String prefixRemovedIngredients = prefixMatcher.replaceAll("").trim();

        String[] lines = prefixRemovedIngredients.split("\n");
        for (String line : lines) {
            // ":" 앞의 내용을 레시피 이름으로 간주하고 이를 무시함
            String[] parts = line.split(":", 2);
            if (parts.length > 1) {
                line = parts[1].trim(); // ":" 뒤의 내용만 사용
            } else {
                line = parts[0].trim(); // ":"가 없는 경우 전체 줄을 사용
            }

            line = line.replaceAll("\\[.*?\\]", "").replaceAll("·", "").trim();

            String[] items = splitByCommaNotInParentheses(line);
            for (String item : items) {
                item = item.trim();

                if (item.startsWith("●") || item.isEmpty()) {
                    continue;
                }

                if (item.contains("약간")) {
                    String ingredientName = item.replace("약간", "").trim();
                    recipeIngredients.add(createRecipeIngredient(ingredientName, "약간"));
                    continue; // "약간" 처리 후 다음 아이템으로 이동
                }

                Matcher matcher = Pattern.compile("([가-힣\\s]+)\\s*(\\([^)]*\\s*\\d+(?:\\.\\d+)?\\s*(?:g|ml)\\)|\\d+(?:\\.\\d+)?\\s*(?:g|ml)(?:\\(.*?\\))?)?").matcher(item);

                if (matcher.find()) {
                    String ingredientName = matcher.group(1).trim();
                    String quantity = matcher.group(2) != null ? matcher.group(2).trim() : "X"; // null 체크 후 trim 호출

                    recipeIngredients.add(createRecipeIngredient(ingredientName, quantity));
                } else {
                    if (!item.isEmpty()) {
                        recipeIngredients.add(createRecipeIngredient(item, "X")); // 수량이 없으면 "X"
                    }
                }
            }
        }

        return recipeIngredients;
    }

    public Ingredient createIngredient(String ingredientName) {
        return Ingredient.builder()
                .name(ingredientName)
                .isSeasoning(OracleBoolean.of(isSeasoning(ingredientName)))
                .build();
    }

    public Ingredient getIngredient(String ingredientName) {
        return ingredientRepository.findByName(ingredientName)
                .orElseThrow(() -> new ApiException(ErrorCode.INGREDIENT_NOT_FOUND));
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
