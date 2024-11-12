package Fridge_Chef.team.recipe.dump;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.repository.IngredientRepository;
import Fridge_Chef.team.ingredient.repository.RecipeIngredientRepository;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.user.domain.Profile;
import Fridge_Chef.team.user.domain.Role;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final DescriptionRepository descriptionRepository;
    private final ImageRepository imageRepository;
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Pattern PREFIX_PATTERN = Pattern.compile("^[\\s●]*(?:재료|주재료)?\\s*");

//    @Value("${recipeRequestUrl}")
    private String baseUrl;

    @Transactional
    public void insertAll() {

        //임의 관리자 생성
        UserId userId = UserId.create();
        Profile profile = new Profile();
        User user = new User(userId, profile, Role.ADMIN);

        userRepository.save(user);

//        for (int start = 1; start <= 1124; start += 10) {
//            if (start == 921) {
//                continue;
//            }
//            int end = start + 9;
//            String url = baseUrl + start + "/" + end;
//            System.out.println(url);
//            JsonNode json = requestRecipe(url);
//
//            for (int i = 0; i < 10; i++) {
//                Recipe recipe = createRecipe(json, i);
//                recipeRepository.save(recipe);
//                saveRecipeWithIngredients(recipe);
//
//                Context context = Context.formMyUserRecipe(recipe.getRecipeIngredients(), recipe.getDescriptions());
//                context = contextRepository.save(context);
//
//                Board board = Board.from(user, recipe);
//                board.setContext(context);
//                boardRepository.save(board);
//            }
//        }

        String url = baseUrl + "1/10";
        JsonNode json = requestRecipe(url);

        for (int i = 0; i < 10; i++) {
            Recipe recipe = createRecipe(json, i);
            recipeRepository.save(recipe);
            saveRecipeWithIngredients(recipe);

            Context context = Context.formMyUserRecipe(recipe.getRecipeIngredients(), recipe.getDescriptions());
//            context = contextRepository.save(context);

            Board board = Board.from(user, recipe);
            board.updateContext(context);
            boardRepository.save(board);
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
        String intro = recipeInfo.get("RCP_NA_TIP").asText();

        Image image = Image.outUri(imageUrl);
        imageRepository.save(image);
        List<Description> descriptions = extractManualsToDescription(recipeInfo);
        List<RecipeIngredient> recipeIngredientList = extractIngredients(ingredients, name);

        return Recipe.builder()
                .name(name)
                .descriptions(descriptions)
                .image(image)
                .intro(intro)
                .recipeIngredients(recipeIngredientList)
                .build();
    }

    private void saveRecipeWithIngredients(Recipe recipe) {

        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {

            Ingredient ingredient = recipeIngredient.getIngredient();
            Optional<Ingredient> existingIngredient = ingredientRepository.findByName(ingredient.getName());

            if (existingIngredient.isPresent()) {
                recipeIngredient.setIngredient(existingIngredient.get());
            } else {
                Ingredient savedIngredient = insertIngredient(ingredient);
                recipeIngredient.setIngredient(savedIngredient);
            }

            recipeIngredientRepository.save(recipeIngredient);
        }

        recipeRepository.save(recipe);
    }

    public List<RecipeIngredient> extractIngredients(String ingredients, String recipeName) {

        String trimmedName = recipeName.replaceAll("\\s+", "");

        List<RecipeIngredient> recipeIngredients = new ArrayList<>();

        Matcher prefixMatcher = PREFIX_PATTERN.matcher(ingredients);
        String prefixRemovedIngredients = prefixMatcher.replaceAll("").trim();

        String[] lines = prefixRemovedIngredients.split("\n");
        for (String line : lines) {
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

                    if (recipeName.equals(ingredientName) || trimmedName.equals(ingredientName)) {
                        continue;
                    }

                    recipeIngredients.add(createRecipeIngredient(ingredientName, "약간"));
                    continue;
                }

                Matcher matcher = Pattern.compile("([가-힣\\s]+)\\s*(\\([^)]*\\s*\\d+(?:\\.\\d+)?\\s*(?:g|ml)\\)|\\d+(?:\\.\\d+)?\\s*(?:g|ml)(?:\\(.*?\\))?)?").matcher(item);

                if (matcher.find()) {
                    String ingredientName = matcher.group(1).trim();
                    String quantity = matcher.group(2) != null ? matcher.group(2).trim() : "X";

                    if (ingredientName.contains("소스") && ingredientName.matches(".*소스\\s+[가-힣]+.*")) {
                        String[] partsAfterSauce = ingredientName.split("소스", 2);
                        ingredientName = partsAfterSauce[0].trim(); // 소스 이전 부분
                        String additionalIngredient = partsAfterSauce[1].trim(); // 소스 이후 부분

                        // 소스 이후 한글
                        if (!additionalIngredient.isEmpty()) {
                            recipeIngredients.add(createRecipeIngredient(additionalIngredient, quantity));
                            continue;
                        }
                    }

                    if (recipeName.equals(ingredientName) || trimmedName.equals(ingredientName)) {
                        continue;
                    }

                    recipeIngredients.add(createRecipeIngredient(ingredientName, quantity));
                } else {
                    if (!item.isEmpty()) {
                        recipeIngredients.add(createRecipeIngredient(item, "X"));
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

        Ingredient ingredient = ingredientRepository.findByName(ingredientName)
                .orElse(Ingredient.builder().name(ingredientName).build());

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

    private List<Description> extractManualsToDescription(JsonNode recipeInfo){
        List<Description> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String manualTextKey = "MANUAL" + String.format("%02d", i);
            String manualImageKey = "MANUAL_IMG" + String.format("%02d", i);

            JsonNode manualNode = recipeInfo.get(manualTextKey);
            JsonNode imageNode = recipeInfo.get(manualImageKey);

            String manualText = (manualNode != null && !manualNode.asText().trim().isEmpty())
                    ? manualNode.asText().replaceAll("\n", " ").trim() : null;
            String imageUri = (imageNode != null && !imageNode.asText().trim().isEmpty()) ? imageNode.asText() : null;

            Image image = null;
            if (imageUri != null) {
                image = Image.outUri(imageUri);
                imageRepository.save(image);
            }
            if (manualText != null || imageUri != null) {
                Description description = new Description(manualText, image);
                list.add(description);
            } else {
                break;
            }
        }

        descriptionRepository.saveAll(list);

        return list;
    }
}
