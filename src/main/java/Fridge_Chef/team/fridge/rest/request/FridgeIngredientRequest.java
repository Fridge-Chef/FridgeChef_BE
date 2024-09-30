package Fridge_Chef.team.fridge.rest.request;

import Fridge_Chef.team.fridge.domain.Storage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FridgeIngredientRequest {

    private String ingredientName;
    private LocalDate expirationDate;
    private Storage storage;
}
