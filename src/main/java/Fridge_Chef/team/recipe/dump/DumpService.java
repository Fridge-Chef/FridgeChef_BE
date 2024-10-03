package Fridge_Chef.team.recipe.dump;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DumpService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pattern PREFIX_PATTERN = Pattern.compile("^[\\s●]*(?:재료|주재료)?\\s*");

    @Value("${recipeRequestUrl}")
    private String baseUrl;

    public void insertAll() {

        for (int start = 1; start <= 1124; start += 10) {
            if (start == 921) {
                continue;
            }
            int end = start + 9;
            String url = baseUrl + start + "/" + end;
            System.out.println(url);
            JsonNode json = requestRecipe(url);

            for (int i = 0; i < 10; i++) {
                Recipe recipe = createRecipe(json, i);
                saveRecipeWithIngredients(recipe);
            }
        }
    }

    private JsonNode requestRecipe(String url) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            String responseBody = response.getBody();

            return objectMapper.readTree(responseBody);
        } catch (Exception e) {
            throw new ApiException(ErrorCode.INVALID_VALUE);
        }
    }

    private Recipe createRecipe(JsonNode json, int idx) {

        JsonNode recipeInfo = json.get("COOKRCP01").get("row").get(idx);

        String name = recipeInfo.get("RCP_NM").asText();
        String ingredients = recipeInfo.get("RCP_PARTS_DTLS").asText();
        String imageUrl = recipeInfo.get("ATT_FILE_NO_MAIN").asText();
        Image image = Image.outUri(imageUrl);

        List<RecipeIngredient> recipeIngredientList = extractIngredients(ingredients);
        List<String> manuals = extractManuals(recipeInfo);

        return Recipe.builder()
                .name(name)
                .manuals(manuals)
                .imageUrl(image)
                .recipeIngredients(recipeIngredientList)
                .build();
    }

    private void saveRecipeWithIngredients(Recipe recipe) {

        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            recipeIngredient.setRecipe(recipe);

            Ingredient ingredient = recipeIngredient.getIngredient();
            Optional<Ingredient> existingIngredient = ingredientRepository.findByName(ingredient.getName());

            if (existingIngredient.isPresent()) {
                recipeIngredient.setIngredient(existingIngredient.get());
            } else {
                Ingredient savedIngredient = insertIngredient(ingredient);
                recipeIngredient.setIngredient(savedIngredient);
            }
        }

        recipeRepository.save(recipe);
    }

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

    private String[] splitByCommaNotInParentheses(String input) {
        List<String> parts = new ArrayList<>();
        int parenthesisLevel = 0;
        StringBuilder currentPart = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == '(') {
                parenthesisLevel++;
            } else if (c == ')') {
                parenthesisLevel--;
            } else if (c == ',' && parenthesisLevel == 0) {
                parts.add(currentPart.toString().trim());
                currentPart.setLength(0);
                continue;
            }
            currentPart.append(c);
        }
        parts.add(currentPart.toString().trim());
        return parts.toArray(new String[0]);
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

    public Ingredient insertIngredient(Ingredient ingredient) {
        Optional<Ingredient> data = ingredientRepository.findByName(ingredient.getName());

        if (data.isPresent()) {
            return data.get(); // 이미 존재하는 경우 해당 Ingredient 반환
        } else {
            return ingredientRepository.save(ingredient); // 새로 저장한 Ingredient 반환
        }
    }

    private List<String> extractManuals(JsonNode recipeInfo) {

        List<String> manuals = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String key = "MANUAL" + String.format("%02d", i);
            JsonNode manualNode = recipeInfo.get(key);

            if (manualNode != null && !manualNode.asText().isEmpty()) {
                String manualText = manualNode.asText().replace("\n", " ").trim();
                manuals.add(manualText);
            }
        }

        return manuals;
    }
}
