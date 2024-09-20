package Fridge_Chef.team.board.service.request;

import Fridge_Chef.team.category.domain.Category;
import Fridge_Chef.team.image.domain.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
public class BoardCreateRequest {
    private List<Step> steps;
    private List<BoardIngredientRequest> ingredients;
    private String title;
    private Image mainImage;
    private Category category;

    public BoardCreateRequest(List<Step> steps, List<BoardIngredientRequest> ingredients, String title, Image mainImage, Category category) {
        this.steps = steps;
        this.ingredients = ingredients;
        this.title = title;
        this.mainImage = mainImage;
        this.category = category;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Step {
        private String description;
        private Image image;
    }


    public static List<Step> formRequest(List<String> descriptions, List<Image> images) {
        if (descriptions.size() != images.size()) {
            throw new IllegalArgumentException("Descriptions and images must have the same size.");
        }

        return IntStream.range(0, descriptions.size())
                .mapToObj(i -> new Step(descriptions.get(i), images.get(i)))
                .collect(Collectors.toList());
    }

    public Long getCategoryId() {
        return category.getId();
    }
}