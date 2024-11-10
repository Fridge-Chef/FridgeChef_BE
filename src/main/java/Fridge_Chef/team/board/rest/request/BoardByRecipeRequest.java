package Fridge_Chef.team.board.rest.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BoardByRecipeRequest {
    @Size(max = 50, message = "이름 중복, 최대 50자 ")
    @NotBlank(message = "레시피 이름은 필수입니다.")
    private String name;

    @Size(max = 200, message = "소개 최대 200자")
    private String description;

    private String dishTime;
    private String dishLevel;
    private String dishCategory;

    private MultipartFile mainImage;

    @Size(min = 2, max = 50, message = "재료는 2~50가지 만 추가 가능합니다.")
    private List<RecipeIngredient> recipeIngredients;

    @Size(min = 1, max = 30, message = "설명은 1~30개 까지 가능합니다.")
    @JsonProperty("descriptions")
    private List<Instructions> descriptions;


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
    public static class Instructions {
        @NotBlank(message = "설명 내용은 필수입니다.")
        private String content;
        private MultipartFile image;
    }
}
