package Fridge_Chef.team.board.rest.request;

import Fridge_Chef.team.board.service.request.BoardCreateRequest;
import Fridge_Chef.team.board.service.request.BoardIngredientRequest;
import Fridge_Chef.team.category.domain.Category;
import Fridge_Chef.team.image.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardPostCreateRequest {
    private List<Step> steps;
    private Long categoryId;
    private String title;
    private List<BoardIngredientRequest> ingredients;
    private MultipartFile mainImage;

    public BoardCreateRequest toCreate(Image mainImage, List<Image> images, List<BoardIngredientRequest> ingredients, Category category) {
        BoardCreateRequest request = new BoardCreateRequest(
                toStep(images),
                ingredients,
                title,
                mainImage,
                category
        );
        return request;
    }

    private List<BoardCreateRequest.Step> toStep(List<Image> images) {
        return BoardCreateRequest.formRequest(
                steps.stream().map(Step::getDescription)
                        .toList(), images);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ingredients {
        private String name;
        private String details;
        private boolean isSeasoning;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private String description;
        private MultipartFile image;
    }

    public List<MultipartFile> getStepImages() {
        List<MultipartFile> list = new ArrayList<>();
        steps.forEach(step -> list.add(step.image));
        return list;
    }
}