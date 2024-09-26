package Fridge_Chef.team.fridge.rest.response;

import Fridge_Chef.team.common.entity.OracleBoolean;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FridgeIngredientResponse {

    private String ingredientName;
    private OracleBoolean isSeasoning;
    private LocalDate expirationDate;
}
