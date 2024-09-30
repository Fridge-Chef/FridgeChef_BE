package Fridge_Chef.team.recipe.rest;

import Fridge_Chef.team.recipe.rest.request.RecipeRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeDetailsResponse;
import Fridge_Chef.team.recipe.rest.response.RecipeNamesResponse;
import Fridge_Chef.team.recipe.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    //레시피 이름 조회 api
    @GetMapping("/")
    public RecipeNamesResponse recipesFromIngredients(@RequestParam("ingredients") List<String> ingredients) {

        RecipeRequest recipeRequest = new RecipeRequest(ingredients);
        List<String> recipeTitles = recipeService.getRecipeTitles(recipeRequest);

        RecipeNamesResponse response = new RecipeNamesResponse(recipeTitles);

        return response;
    }

    //레시피 이름 조회 api
    //이름 -> 레시피 이름들

    //특정 레시피 상세 조회 api
    @GetMapping("/details")
    public RecipeDetailsResponse recipeInfo(@RequestParam("recipe_name") String recipeName) {

        RecipeDetailsResponse response = recipeService.getRecipeDetails(recipeName);

        return response;
    }
}
