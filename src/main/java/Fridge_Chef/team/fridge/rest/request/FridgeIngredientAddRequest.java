package Fridge_Chef.team.fridge.rest.request;

import Fridge_Chef.team.fridge.domain.Storage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FridgeIngredientAddRequest {

    private String ingredientName;
    private Storage storage;
}
