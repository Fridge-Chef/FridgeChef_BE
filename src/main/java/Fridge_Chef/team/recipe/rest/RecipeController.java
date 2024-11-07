package Fridge_Chef.team.recipe.rest;

import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.image.service.ImageService;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.recipe.repository.model.RecipeSearchSortType;
import Fridge_Chef.team.recipe.rest.request.RecipeCreateRequest;
import Fridge_Chef.team.recipe.rest.request.RecipePageRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import Fridge_Chef.team.recipe.service.RecipeIngredientService;
import Fridge_Chef.team.recipe.service.RecipeService;
import Fridge_Chef.team.user.domain.UserId;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;


@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeIngredientService recipeIngredientService;
    private final RecipeService recipeService;
    private final ImageService imageService;

    //나만의 레시피 생성
    @PostMapping("/")
    @ResponseStatus(HttpStatus.CREATED)
    public void create(
            @AuthenticationPrincipal AuthenticatedUser user,
            @Valid @ModelAttribute RecipeCreateRequest request) {

        UserId userId = user.userId();

        List<RecipeIngredient> recipeIngredients = recipeIngredientService.getOrCreate(request);
        List<Description> descriptions = recipeService.createDescriptions(userId, request.getDescriptions());

        recipeService.createMyRecipe(userId, request, recipeIngredients, descriptions);
    }


    @GetMapping("/")
    public Page<RecipeSearchResponse> search(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "", required = false) String[] must,
            @RequestParam List<String> ingredients,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "50", required = false) int size,
            @RequestParam(defaultValue = "MATCH", required = false) RecipeSearchSortType sort) {

        List<String> mustList = Arrays.asList(must);

        RecipePageRequest request = new RecipePageRequest(page, size, sort);

        return recipeService.searchRecipe(request, mustList, ingredients, AuthenticatedUser.anonymousUser(user));
    }
}
