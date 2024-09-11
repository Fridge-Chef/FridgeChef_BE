package Fridge_Chef.team.recipe.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeNamesResponse {

    List<String> recipeNames;
}
