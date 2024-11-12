package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class Context {
    private String dishTime;
    private String dishLevel;
    private String dishCategory;
    @Column(length = 500)
    private String pathIngredient;
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<RecipeIngredient> boardIngredients;
    @OneToMany(orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Description> descriptions;

    private Context(String dishTime, String dishLevel, String dishCategory, List<RecipeIngredient> boardIngredients, List<Description> descriptions) {
        this.dishLevel = dishLevel;
        this.dishTime = dishTime;
        this.dishCategory = dishCategory;
        this.boardIngredients = boardIngredients;
        this.descriptions = descriptions;
        slicePathIngredient();
    }

    public Context(List<RecipeIngredient> boardIngredients, List<Description> descriptions) {
        this("", "", "", boardIngredients, descriptions);
    }

    public static Context formMyUserRecipe(
            List<RecipeIngredient> boardIngredients,
            List<Description> descriptions) {
        return new Context(boardIngredients, descriptions);
    }

    public static Context formMyUserRecipe(
            String dishTime,
            String dishLevel,
            String dishCategory,
            List<RecipeIngredient> boardIngredients,
            List<Description> descriptions) {
        return new Context(dishTime, dishLevel, dishCategory, boardIngredients, descriptions);
    }

    public void update(List<RecipeIngredient> ingredients, List<Description> descriptions, String dishTime, String dishLevel, String dishCategory) {
        this.boardIngredients.clear();
        this.descriptions.clear();
        this.boardIngredients.addAll(ingredients);
        this.descriptions.addAll(descriptions);
        this.dishTime = dishTime;
        this.dishLevel = dishLevel;
        this.dishCategory = dishCategory;
    }

    public void update(List<Description> descriptions, String dishTime, String dishLevel, String dishCategory) {
        this.descriptions.clear();
        this.descriptions.addAll(descriptions);
        this.dishTime = dishTime;
        this.dishLevel = dishLevel;
        this.dishCategory = dishCategory;
    }

    public void slicePathIngredient() {
        pathIngredient = boardIngredients.stream()
                .map(ingredient -> ingredient.getIngredient().getName())
                .collect(Collectors.joining(","));
    }

    public void addRecipeIngredient(RecipeIngredient recipeIngredient) {
        this.boardIngredients.add(recipeIngredient);
    }
}
