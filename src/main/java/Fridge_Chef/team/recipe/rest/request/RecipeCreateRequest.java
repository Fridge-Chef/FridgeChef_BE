package Fridge_Chef.team.recipe.rest.request;

import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateRequest {

    private String name;
    private String intro;
    private int cookTime;
    private List<RecipeIngredient> recipeIngredients;
    private String imageUrl;
    private List<Description> descriptions;

}
