package Fridge_Chef.team.board.service.request;

import Fridge_Chef.team.board.rest.request.BoardByRecipeRequest;
import Fridge_Chef.team.board.rest.request.BoardByRecipeUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class BoardRecipeIngredientFindRequest {

    private List<RecipeIngredient> recipeIngredients;
    @Getter
    @AllArgsConstructor
    public static class RecipeIngredient {
        private Long id;
        private String name;
        private String details;
    }

    public static BoardRecipeIngredientFindRequest from(BoardByRecipeRequest request) {
        List<BoardRecipeIngredientFindRequest.RecipeIngredient> recipeIngredients =
                request.getRecipeIngredients().stream()
                        .map(ingredient -> new BoardRecipeIngredientFindRequest.RecipeIngredient(
                                0L,
                                ingredient.getName(),
                                ingredient.getDetails()))
                        .collect(Collectors.toList());

        return new BoardRecipeIngredientFindRequest(recipeIngredients);
    }

    public static BoardRecipeIngredientFindRequest from(BoardByRecipeUpdateRequest request) {
        List<BoardRecipeIngredientFindRequest.RecipeIngredient> recipeIngredients =
                request.getRecipeIngredients().stream()
                        .map(ingredient -> new BoardRecipeIngredientFindRequest.RecipeIngredient(
                                ingredient.getId(),
                                ingredient.getName(),
                                ingredient.getDetails()))
                        .collect(Collectors.toList());

        return new BoardRecipeIngredientFindRequest(recipeIngredients);
    }
}
