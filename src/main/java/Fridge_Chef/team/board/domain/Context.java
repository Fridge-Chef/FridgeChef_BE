package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class Context {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    private String dishTime;
    private String dishLevel;
    private String dishCategory;

    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<RecipeIngredient> boardIngredients;
    @OneToMany(fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.PERSIST)
    private List<Description> descriptions;

    public Context(List<RecipeIngredient> boardIngredients, List<Description> descriptions) {
        this.dishTime = "";
        this.dishLevel = "";
        this.dishCategory = "";
        this.boardIngredients = boardIngredients;
        this.descriptions = descriptions;
    }

    public Context(String dishTime, String dishLevel, String dishCategory, List<RecipeIngredient> boardIngredients, List<Description> descriptions) {
        this.dishLevel = dishLevel;
        this.dishTime = dishTime;
        this.dishCategory = dishCategory;
        this.boardIngredients = boardIngredients;
        this.descriptions = descriptions;
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
        this.boardIngredients = ingredients;
        this.descriptions = descriptions;
        this.dishTime = dishTime;
        this.dishLevel = dishLevel;
        this.dishCategory = dishCategory;
    }
}
