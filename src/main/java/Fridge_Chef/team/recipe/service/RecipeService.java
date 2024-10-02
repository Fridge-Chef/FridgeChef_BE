package Fridge_Chef.team.recipe.service;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.board.repository.BoardRepository;
import Fridge_Chef.team.board.repository.DescriptionRepository;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.image.repository.ImageRepository;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.ingredient.rest.response.IngredientResponse;
import Fridge_Chef.team.ingredient.service.IngredientService;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.domain.RecipeDescription;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.recipe.rest.request.RecipeRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeDetailsResponse;
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

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final DescriptionRepository descriptionRepository;
    private final BoardRepository boardRepository;
    private final ImageRepository imageRepository;
    private final IngredientService ingredientService;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Value("${recipeRequestUrl}")
    private String baseUrl;

    public List<String> getRecipeTitles(RecipeRequest request) throws ApiException {

        String url = baseUrl + "/RCP_PARTS_DTLS=" + request.toString();
        JsonNode json = requestRecipe(url);
        List<String> recipeNames = extractRecipeNames(json);

        return recipeNames;
    }

    @Transactional
    public RecipeDetailsResponse getRecipeDetails(String recipeName) throws ApiException {

        Optional<Recipe> optionalRecipe = recipeRepository.findByName(recipeName);
        if (!optionalRecipe.isEmpty()) {
            Recipe recipe = optionalRecipe.get();
            return recipeToDto(recipe);
        }

        String url = baseUrl + "/RCP_NM=" + recipeName;
        JsonNode json = requestRecipe(url);
        Recipe recipe = createRecipe(json);

        for (RecipeIngredient recipeIngredient : recipe.getRecipeIngredients()) {
            recipeIngredient.setRecipe(recipe);
            if (recipeIngredient.getIngredient().getId() == null) {
                ingredientService.insertIngredient(recipeIngredient.getIngredient());
            }
        }

//        boardRepository.save(Board.from(null,recipe)); // 통합전 user=?
        recipeRepository.save(recipe);

        return recipeToDto(recipe);
    }

    private Recipe createRecipe(JsonNode json) {

        JsonNode recipeInfo = json.get("COOKRCP01").get("row").get(0);

        String name = recipeInfo.get("RCP_NM").asText();
        String ingredients = recipeInfo.get("RCP_PARTS_DTLS").asText();
        String imageUrl = recipeInfo.get("ATT_FILE_NO_MAIN").asText();
        Image mainImage = imageRepository.save(Image.outUri(imageUrl));

        List<RecipeIngredient> recipeIngredientList = ingredientService.extractIngredients(ingredients);
        List<Description> manuals = extractManualsToDescription(recipeInfo);

        return Recipe.builder()
                .name(name)
                .descriptions(manuals)
                .imageUrl(mainImage)
                .recipeIngredients(recipeIngredientList)
                .build();
    }

    private List<Description> extractManualsToDescription(JsonNode json){
        List<Description> list = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            JsonNode manualNode = json.get( "MANUAL" + String.format("%02d", i));
            JsonNode imageNode = json.get( "MANUAL_IMG" + String.format("%02d", i));
            String manualText = (manualNode != null && !manualNode.asText().trim().isEmpty()) ? manualNode.asText() : null;
            String imageUri = (imageNode != null && !imageNode.asText().trim().isEmpty()) ? imageNode.asText() : null;
            if (manualText != null || imageUri != null) {
                Description description = new Description(manualText, Image.outUri(imageUri));
                descriptionRepository.save(description);
                list.add(description);
            }
        }
        return list;
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

    private List<String> extractRecipeNames(JsonNode json) {

        List<String> recipeNames = new ArrayList<>();

        JsonNode cookRcpNode = json.path("COOKRCP01");
        JsonNode rowArray = cookRcpNode.path("row");

        if (rowArray.isArray()) {
            for (JsonNode node : rowArray) {
                JsonNode recipeName = node.get("RCP_NM");
                if (recipeName != null) {
                    recipeNames.add(recipeName.asText());
                }
            }
        }

        return recipeNames;
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

    private RecipeDetailsResponse recipeToDto(Recipe recipe) {

        List<IngredientResponse> ingredients = recipe.getRecipeIngredients().stream()
                .map(recipeIngredient -> IngredientResponse.builder()
                        .name(recipeIngredient.getIngredient().getName())
                        .quantity(recipeIngredient.getQuantity())
                        .build())
                .toList();

        return RecipeDetailsResponse.builder()
                .name(recipe.getName())
                .ingredients(ingredients)
                .manuals(recipe.getManuals())
                .imageUrl(recipe.getImageUrl().getLink())
                .build();
    }
}
