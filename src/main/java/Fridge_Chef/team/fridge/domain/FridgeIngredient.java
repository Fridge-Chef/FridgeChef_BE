package Fridge_Chef.team.fridge.domain;

import Fridge_Chef.team.ingredient.domain.Ingredient;
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

    @ManyToOne
    @JoinColumn(name = "fridge_id")
    private Fridge fridge;

    @ManyToOne
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    private LocalDate expirationDate;
    private Storage storage;

    public void updateExpirationDate(LocalDate exp) {
        this.expirationDate = exp;
    }

    public void updateStorage(Storage storage) {
        this.storage = storage;
    }
}
