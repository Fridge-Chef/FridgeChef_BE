package Fridge_Chef.team.recipe.rest.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeRequest {

    //레시피 요청 dto
    //ingredients 필드가 아예 없거나, 아무 재료도 없다면? -> not valid
    @NotNull
    @Size(min = 1)
    private List<String> ingredients;

    @Override
    public String toString() {
        return String.join(",", ingredients);
    }
}
