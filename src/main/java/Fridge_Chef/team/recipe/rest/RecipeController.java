package Fridge_Chef.team.recipe.rest;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.recipe.rest.request.RecipeRequest;
import Fridge_Chef.team.recipe.rest.request.SampleRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeDetailsResponse;
import Fridge_Chef.team.recipe.rest.response.RecipeNamesResponse;
import Fridge_Chef.team.recipe.rest.response.SampleResponse;
import Fridge_Chef.team.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @GetMapping()
    public String info() {
        return "web request success";
    }

    @PostMapping("/test")
    public SampleResponse docs(@RequestBody SampleRequest dto) {
        return new SampleResponse(dto.getName(), dto.getValue());
    }

    //레시피 이름 조회 api
    @GetMapping("/recipes")
    public ResponseEntity<?> recipesFromIngredients(@RequestParam("ingredients") List<String> ingredients) throws ApiException {

        System.out.println("***");
        try {
            RecipeRequest recipeRequest = new RecipeRequest(ingredients);
            List<String> recipeTitles = recipeService.getRecipeTitles(recipeRequest);

            RecipeNamesResponse response = new RecipeNamesResponse(recipeTitles);

            return ResponseEntity.ok().body(response);
        } catch (ApiException e) {
            throw e;
        }
    }

    //레시피 이름 조회 api
    //이름 -> 레시피 이름들
//    @GetMapping("/recipes")
//    public void recipesFromName(@RequestParam("recipe_name") String name) {
//
//    }

    //특정 레시피 상세 조회 api
    @GetMapping("/recipes/details")
    public ResponseEntity<?> recipeInfo(@RequestParam("recipe_name") String recipeName) throws ApiException {

        try {
            RecipeDetailsResponse response = recipeService.getRecipeDetails(recipeName);
            return ResponseEntity.ok().body(response);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }
}
