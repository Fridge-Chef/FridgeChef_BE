package Fridge_Chef.team.recipe.rest.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeDetailsResponse {
    String name;
    List<String> ingredients;
    String instructions;
    String imageUrl;
}
