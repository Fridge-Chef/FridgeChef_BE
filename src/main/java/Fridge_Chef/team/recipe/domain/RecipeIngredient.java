package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.board.domain.Context;
import Fridge_Chef.team.ingredient.domain.Ingredient;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Getter
@Builder
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "context_id")
    private Context context;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private String quantity;


    public RecipeIngredient(Ingredient ingredient, String detail) {
        this.ingredient = ingredient;
        this.quantity = detail;
    }

    public static RecipeIngredient ofMyRecipe(Ingredient ingredient,  String detail){
        return new RecipeIngredient(ingredient, detail);
    }

    public RecipeIngredient update(Ingredient ingredient, String details) {
        this.ingredient=ingredient;
        this.quantity=details;
        return this;
    }
}
