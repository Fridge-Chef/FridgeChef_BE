package Fridge_Chef.team.recipe.rest.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCreateRequest {

    @Size(max = 50, message = "이름 중복, 최대 50자")
    @NotBlank(message = "이름은 필수입니다.")
    private String name;

    @Size(max = 200, message = "소개 최대 200자")
    private String intro;

    private String cookTime;
    private String difficult;
    private String category;
    private MultipartFile mainImage;

    @Size(min = 2, max = 50, message = "재료는 2~50가지만 가능합니다.")
    private List<RecipeIngredient> recipeIngredients;

    @Size(min = 1, max = 30, message = "조리 메뉴얼은 1~30개만 가능합니다.")
    private List<Description> descriptions;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RecipeIngredient {
        @NotBlank(message = "재료 이름은 필수입니다.")
        private String name;
        private String details;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Description {
        @NotBlank(message = "설명 내용은 필수입니다.")
        private String content;
        private MultipartFile image;
    }
}
