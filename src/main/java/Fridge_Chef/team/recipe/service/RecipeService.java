package Fridge_Chef.team.recipe.service;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.recipe.repository.RecipeDslRepository;
import Fridge_Chef.team.recipe.rest.request.RecipePageRequest;
import Fridge_Chef.team.recipe.rest.response.RecipeSearchResponse;
import Fridge_Chef.team.user.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeDslRepository recipeDslRepository;

    @Transactional(readOnly = true)
    public Page<RecipeSearchResponse> searchRecipe(RecipePageRequest request, List<String> must, List<String> ingredients, Optional<UserId> userId) {
        if (must.size() + ingredients.size() == 0) {
            throw new ApiException(ErrorCode.RECIPE_INGREDIENT_NULL);
        }
        return recipeDslRepository.findRecipesByIngredients( PageRequest.of(request.page(), request.size()), request, must, ingredients, userId);
    }
}
