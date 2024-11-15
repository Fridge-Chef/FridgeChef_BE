package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.image.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Recipe {
    private String name;
    private String category;
    private String intro;
    private String cookTime;
    private Difficult difficult;
    private Image image;
    private Board board;
    private List<Description> descriptions;
    private List<RecipeIngredient> recipeIngredients;
}