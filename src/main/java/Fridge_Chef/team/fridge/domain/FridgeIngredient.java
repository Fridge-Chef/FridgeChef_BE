package Fridge_Chef.team.fridge.domain;

import Fridge_Chef.team.ingredient.domain.Ingredient;
import Fridge_Chef.team.ingredient.domain.IngredientCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "fridge_ingredient")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FridgeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private LocalDate expirationDate;
    private Storage storage;
    private IngredientCategory ingredientCategory;

    public FridgeIngredient(Fridge fridge, Ingredient ingredient, Storage storage) {
        this.fridge = fridge;
        this.ingredient = ingredient;
        this.storage = storage;
    }

    public void updateExpirationDate(LocalDate exp) {
        this.expirationDate = exp;
    }

    public void updateCategory(IngredientCategory category) {
        this.ingredientCategory = category;
    }

    public void updateStorage(Storage storage) {
        this.storage = storage;
    }
}
