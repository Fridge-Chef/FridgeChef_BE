package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.image.domain.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Table
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(generator = "uuid2")
    @UuidGenerator
    @Column(name = "recipe_id", columnDefinition = "BINARY(16)")
    private UUID id;

    private String name;
    private String intro;
    private int cookTime;

    @OneToOne(fetch = FetchType.LAZY)
    private Image image;

    @Column(name = "recipe_description")
    @OneToMany(fetch = FetchType.LAZY)
    private List<Description> descriptions;

    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    private List<RecipeIngredient> recipeIngredients;
}