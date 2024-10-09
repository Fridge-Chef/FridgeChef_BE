package Fridge_Chef.team.recipe.rest.response;

import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeResponse {

    private String name;
    private String intro;
    private Image image;
    private List<RecipeIngredient> recipeIngredients;
    private List<Description> descriptions;
}
