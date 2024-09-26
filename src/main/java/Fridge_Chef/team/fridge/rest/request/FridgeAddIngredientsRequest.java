package Fridge_Chef.team.fridge.rest.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FridgeAddIngredientsRequest {

    private List<FridgeIngredientRequest> ingredients;
}
