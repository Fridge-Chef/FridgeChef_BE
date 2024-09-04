package Fridge_Chef.team.recipe.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.recipe.domain.Recipe;
import Fridge_Chef.team.recipe.repository.RecipeRepository;
import Fridge_Chef.team.recipe.rest.request.RecipeRequest;
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

import java.util.*;

@RequiredArgsConstructor
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${recipeRequestUrl}")
    private String baseUrl;

    //재료 AND조건으로 recipe titles 검색 메서드
    public List<String> getRecipeTitles(RecipeRequest request) throws ApiException {

        //요청 url 생성
        String url = baseUrl + "/RCP_PARTS_DTLS=" + request.toString();
        //레시피 조회 요청
        JsonNode json = requestRecipe(url);
        //레시피 이름 추출
        List<String> recipeNames = extractRecipeNames(json);

        return recipeNames;
    }

    //recipe 정보 json으로 리턴
    public Map<String, Object> getRecipeDetails(String recipeName) throws ApiException {

        Optional<Recipe> optionalRecipe = recipeRepository.findByName(recipeName);
        //recipe 정보가 db에 저장이 되어 있음
        if (!optionalRecipe.isEmpty()) {
            Recipe recipe = optionalRecipe.get();
            return recipeToJson(recipe);
        }

        //요청 url 생성
        String url = baseUrl + "/RCP_NM=" + recipeName;
        System.out.println(url);
        //레시피 조회 요청
        JsonNode json = requestRecipe(url);
        System.out.println(json);
        //레시피 정보 추출
        Recipe recipe = extractRecipeDetails(json);
        recipeRepository.save(recipe);

        return recipeToJson(recipe);
    }

    //레시피 조회 요청 메서드
    private JsonNode requestRecipe(String url) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        //요청 body 필요 없음. 헤더만 적재
        HttpEntity<String> request = new HttpEntity<>(null, headers);

        //get 요청
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            String responseBody = response.getBody();

            return objectMapper.readTree(responseBody);
        } catch (Exception e) {
            //요쳥 실패 400 bad request?
            throw new ApiException(ErrorCode.INVALID_VALUE);
        }
    }

    //JSON에서 레시피 이름 추출 메서드
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

    //json에서 레시피 상세 정보 추출 -> Recipe 엔티티 반환
    private Recipe extractRecipeDetails(JsonNode json) {

        JsonNode recipeInfo = json.get("COOKRCP01").get("row").get(0);

        //json 추출
        String name = recipeInfo.get("RCP_NM").asText();
        String category = recipeInfo.get("RCP_PAT2").asText();
//        String ingredients = recipeInfo.get("RCP_PARTS_DTLS").asText();
        String instructions = extractInstructions(recipeInfo);
        String imageUrl = recipeInfo.get("ATT_FILE_NO_MAIN").asText();

        //엔티티 반환
        return Recipe.builder()
                .name(name)
                .category(category)
                .instructions(instructions)
                .imageUrl(imageUrl)
                .build();
    }

    //요리 순서 추출
    private String extractInstructions(JsonNode recipeInfo) {

        StringBuilder instructions = new StringBuilder();

        for (int i = 1; i <= 20; i++) {
            String manual = recipeInfo.get("MANUAL" + String.format("%02d", i)).asText();
            if (manual != null && !manual.isEmpty()) {
                instructions.append(manual).append("\n");
            }
        }
        return instructions.toString().trim();
    }

    //엔티티 -> json 변환
    private Map<String, Object> recipeToJson(Recipe recipe) {

        Map<String, Object> recipeDetails = new HashMap<>();
        recipeDetails.put("name", recipe.getName());
        recipeDetails.put("category", recipe.getCategory());
        recipeDetails.put("instructions", recipe.getInstructions());
        recipeDetails.put("imageUrl", recipe.getImageUrl());

        return recipeDetails;
    }
}
