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

    @OneToMany(fetch = FetchType.LAZY)
    private List<RecipeIngredient> boardIngredients;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Description> descriptions;

    public Context(List<RecipeIngredient> boardIngredients, List<Description> descriptions) {
        this.boardIngredients = boardIngredients;
        this.descriptions = descriptions;
    }

    public static Context formMyUserRecipe(List<RecipeIngredient> boardIngredients, List<Description> descriptions) {
        return new Context(boardIngredients, descriptions);
    }

    public void updateIngredients(List<RecipeIngredient> ingredients) {
        this.boardIngredients=ingredients;
    }

    public void updateDescriptions(List<Description> descriptions) {
        this.descriptions=descriptions;
    }
}
