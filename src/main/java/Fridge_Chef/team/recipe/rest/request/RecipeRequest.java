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

    @NotNull
    @Size(min = 1)
    private List<String> ingredients;

    @Override
    public String toString() {
        return String.join(",", ingredients);
    }
}
