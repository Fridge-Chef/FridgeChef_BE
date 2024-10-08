package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.recipe.domain.RecipeDescription;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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

        List<RecipeIngredient> copiedIngredients = new ArrayList<>();
        List<Description> copiedDescriptions = new ArrayList<>();

        for (RecipeIngredient ingredient : boardIngredients) {
            copiedIngredients.add(ingredient);
        }

        for (Description description : descriptions) {
            copiedDescriptions.add(description);
        }
        return new Context(copiedIngredients, copiedDescriptions);
    }

    public void updateIngredients(List<RecipeIngredient> ingredients) {
        this.boardIngredients=ingredients;
    }

    public void updateDescriptions(List<Description> descriptions) {
        this.descriptions=descriptions;
    }

    public List<RecipeDescription> toRecipeDescription(){
        List<RecipeDescription> list = new ArrayList<>();
        descriptions.forEach(description -> list.add(new RecipeDescription(description.getDescription(),description.getLink())));
        return list;
    }
}
