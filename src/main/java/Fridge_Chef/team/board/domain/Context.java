package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.recipe.domain.Recipe;
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
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeIngredient> boardIngredients;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Description> descriptions;

    private Context(List<RecipeIngredient> boardIngredients, List<Description> descriptions) {
        this.boardIngredients = boardIngredients;
        this.descriptions = descriptions;
    }

    public static Context toMyUserRecipe(List<RecipeIngredient> boardIngredients, List<Description> descriptions) {
        return new Context(boardIngredients, descriptions);
    }

    public static Context fromRecipe(Recipe recipe) {
        List<RecipeIngredient> boardIngredients = recipe.getRecipeIngredients();
        List<Description> descriptions1 = Description.fromRecipe(recipe);
        return new Context(boardIngredients, descriptions1);
    }
}
