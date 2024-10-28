package Fridge_Chef.team.ingredient.rest.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientSearchResponse {

    private List<String> ingredientNames;
}
