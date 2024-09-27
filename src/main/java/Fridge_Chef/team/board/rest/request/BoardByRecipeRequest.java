package Fridge_Chef.team.board.rest.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BoardByRecipeRequest {
    private String name;
    private String description;
    private MultipartFile mainImage;
    private List<RecipeIngredient> recipeIngredients;
    private List<Instructions> instructions;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecipeIngredient {
        private String name;
        private String details;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Instructions {
        private String content;
        private MultipartFile image;
    }
}
