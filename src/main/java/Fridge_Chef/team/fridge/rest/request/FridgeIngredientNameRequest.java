package Fridge_Chef.team.fridge.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FridgeIngredientNameRequest {

    private String ingredientName;
}
