package Fridge_Chef.team.ingredient.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientResponse {

    private String name;
    private String quantity;
}
