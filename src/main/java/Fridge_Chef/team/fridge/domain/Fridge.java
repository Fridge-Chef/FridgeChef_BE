package Fridge_Chef.team.fridge.domain;

import Fridge_Chef.team.user.domain.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Fridge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "fridge", fetch = FetchType.LAZY,orphanRemoval = true,cascade = CascadeType.PERSIST)
    private List<FridgeIngredient> fridgeIngredients;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Fridge(List<FridgeIngredient> fridgeIngredients, User user) {
        this.fridgeIngredients = fridgeIngredients;
        this.user = user;
    }

    public static Fridge setup(User user) {
        return new Fridge(List.of(),user);
    }

    public Fridge delete(FridgeIngredient fridgeIngredient) {
        fridgeIngredients.remove(fridgeIngredient);
        return this;
    }
}
