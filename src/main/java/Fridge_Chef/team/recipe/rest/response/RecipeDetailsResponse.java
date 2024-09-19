package Fridge_Chef.team.recipe.rest.response;

import Fridge_Chef.team.ingredient.rest.response.IngredientResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailsResponse {

    private String name;
    private List<IngredientResponse> ingredients;
    private List<String> manuals;
    private String imageUrl;
}
