package Fridge_Chef.team.board.rest.request;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Getter

public class BoardByRecipeUpdateRequest {
    private Long id;
    private String title;
    private String description;
    private MultipartFile mainImage;
    private Long mainImageId;
    private boolean isMainImageChange;
    private List<BoardByRecipeUpdateRequest.RecipeIngredient> recipeIngredients;
    private List<BoardByRecipeUpdateRequest.Instructions> instructions;

    @Getter
    @AllArgsConstructor
    public static class RecipeIngredient {
        private Long id;
        private String name;
        private String details;
    }

    @Getter
    @AllArgsConstructor
    public static class Instructions {
        private Long id;
        private String content;
        private MultipartFile image;
        private boolean isMainImageChange;
    }

    public BoardByRecipeUpdateRequest(long board, String title, String description, MultipartFile mainImage, boolean isMainImageChange,
                                      List<BoardByRecipeUpdateRequest.RecipeIngredient> recipeIngredient,
                                      List<BoardByRecipeUpdateRequest.Instructions> Instructions) {
        this.id = board;
        this.title = title;
        this.description = description;
        this.mainImage = mainImage;
        this.isMainImageChange = isMainImageChange;
        List<RecipeIngredient> recipeIngredients1 = new ArrayList<>();
        List<Instructions> inst = new ArrayList<>();
        for(BoardByRecipeUpdateRequest.RecipeIngredient recipes: recipeIngredient){
            recipeIngredients1.add(new RecipeIngredient(recipes.getId(),recipes.getName(),recipes.getDetails()));
        }
        for(BoardByRecipeUpdateRequest.Instructions entity: Instructions){
            inst.add(new Instructions(entity.getId(),entity.getContent(),null,false));
        }
        this.instructions = inst;
        this.recipeIngredients = recipeIngredients1;
    }
}
