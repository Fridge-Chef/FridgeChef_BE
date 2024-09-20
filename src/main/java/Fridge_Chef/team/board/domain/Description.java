package Fridge_Chef.team.board.domain;


import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.recipe.domain.Recipe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Description {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private Image image;

    public Description(String description, Image image) {
        this.description = description;
        this.image = image;
    }

    public static List<Description> fromRecipe(Recipe recipe) {
        List<Description> descriptions = new ArrayList<>();
//        for(var recipeDescription :recipe.getRecipeIngredients()){
//            descriptions.add(fromRecipe(recipe));
//        }
        return descriptions;
    }
//    public Description fromRecipe(RecipeDescription recipeDescription){
//        return new Description(recipeDescription.getDescription(),recipeDescription.getImage());
//    }
}