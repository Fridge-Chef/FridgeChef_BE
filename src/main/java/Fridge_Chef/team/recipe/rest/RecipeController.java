package Fridge_Chef.team.recipe.rest;

import Fridge_Chef.team.recipe.repository.model.RecipeSearchSortType;
import Fridge_Chef.team.recipe.rest.request.RecipePageRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import Fridge_Chef.team.recipe.service.RecipeService;
import Fridge_Chef.team.user.rest.model.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;


@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/")
    public Page<RecipeSearchResponse> search(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestParam(defaultValue = "", required = false) String[] must,
            @RequestParam(defaultValue = "", required = false) String[] ingredients,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "50", required = false) int size,
            @RequestParam(defaultValue = "MATCH", required = false) RecipeSearchSortType sort) {
        RecipePageRequest request = new RecipePageRequest(page, size, sort);

        return recipeService.searchRecipe(request, Arrays.asList(must), Arrays.asList(ingredients), AuthenticatedUser.anonymousUser(user));
    }
}
