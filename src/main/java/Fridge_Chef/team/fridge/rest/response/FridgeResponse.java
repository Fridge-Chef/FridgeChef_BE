package Fridge_Chef.team.fridge.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FridgeResponse {

    private String IngredientName;
    private LocalDate expirationDate;
}
