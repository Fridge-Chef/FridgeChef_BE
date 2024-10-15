package Fridge_Chef.team.fridge.rest.request;

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
    private String IngredientCategory;
    private LocalDate expirationDate;
}
