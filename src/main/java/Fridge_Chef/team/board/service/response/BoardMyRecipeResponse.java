package Fridge_Chef.team.board.service.response;

import Fridge_Chef.team.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardMyRecipeResponse {
    private String title;
    private double rating;
    private String mainImage;
    private List<OwnedIngredientResponse> ownedIngredients;
    private List<RecipeIngredientResponse> recipeIngredients;
    private List<StepResponse> instructions;
    private Long boardId;

    public static BoardMyRecipeResponse of(Board board) {

        var ownedIngredients = board.getContext().getBoardIngredients().stream()
                .map(ingredient -> new OwnedIngredientResponse(ingredient.getId(),ingredient.getIngredient().getName()))
                .collect(Collectors.toList());

        var recipeIngredients = board.getContext().getBoardIngredients().stream()
                .map(ingredient -> new RecipeIngredientResponse(ingredient.getIngredient().getId(), ingredient.getIngredient().getName(), ingredient.getQuantity()))
                .collect(Collectors.toList());

        var instructions = board.getContext().getDescriptions().stream()
                .map(step -> new StepResponse(step.getDescription(), step.getLink()))
                .collect(Collectors.toList());

        return new BoardMyRecipeResponse(board.getTitle(),
                board.getTotalStar(),
                board.getMainImageLink(),
                ownedIngredients,
                recipeIngredients,
                instructions,
                board.getId());
    }

    @Getter
    @AllArgsConstructor
    public static class OwnedIngredientResponse {
        private Long id;
        private String name;
    }

    @Getter
    @AllArgsConstructor
    public static class RecipeIngredientResponse {
        private Long id;
        private String name;
        private String details;
    }

    @Getter
    @AllArgsConstructor
    public static class StepResponse {
        private String content;
        private String imageLink;
    }
}
