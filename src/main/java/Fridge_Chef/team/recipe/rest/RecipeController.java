package Fridge_Chef.team.recipe.rest;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.recipe.rest.request.RecipeCreateRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeResponse;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResult;
import Fridge_Chef.team.recipe.service.RecipeService;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    //나만의 레시피 생성
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@AuthenticationPrincipal AuthenticatedUser user, @RequestBody RecipeCreateRequest request) {

        UserId userId = user.userId();

        recipeService.createMyRecipe(userId, request);
    }

    //레시피 조회
    @GetMapping("/")
    public RecipeSearchResult search(
            @RequestParam("ingredients") List<String> ingredients,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size) {

        if (ingredients.size() == 0) {
            throw new ApiException(ErrorCode.INGREDIENT_INVALID);
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        RecipeSearchResult response = recipeService.searchRecipe(pageRequest, ingredients);

        return response;
    }

    //레시피 이름 조회 api
    //이름 -> 레시피 이름들

    //특정 레시피 상세 조회 api
    @GetMapping("/details")
    public RecipeResponse recipeInfo(@RequestParam("recipe_name") String recipeName) {

        RecipeResponse response = null;

        return response;
    }
}
