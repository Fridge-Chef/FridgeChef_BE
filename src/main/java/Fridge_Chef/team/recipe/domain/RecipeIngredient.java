package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.board.domain.Board;
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
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;
    private String quantity;


    public RecipeIngredient(Ingredient ingredient, String detail) {
        this.ingredient = ingredient;
        this.quantity = detail;
    }

    public RecipeIngredient(Ingredient ingredient, String detail,Board board) {
        this.ingredient = ingredient;
        this.quantity = detail;
        this.board=board;
    }

    public static RecipeIngredient ofMyRecipe(Ingredient ingredient,  String detail){
        return new RecipeIngredient(ingredient, detail);
    }
    public static RecipeIngredient ofMyRecipe(Ingredient ingredient,  String detail,Board board){
        return new RecipeIngredient(ingredient, detail,board);
    }

    public RecipeIngredient update(Ingredient ingredient, String details) {
        this.ingredient=ingredient;
        this.quantity=details;
        return this;
    }
}
