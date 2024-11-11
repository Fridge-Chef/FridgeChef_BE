package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.Description;
import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.image.domain.Image;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.ArrayList;
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
    @Column(name = "recipe_id", columnDefinition = "VARCHAR2(36)")
    private UUID id;
    private String name;
    private String category;
    private String intro;
    private String cookTime;
    @Enumerated(EnumType.STRING)
    private Difficult difficult;
    @OneToOne(fetch = FetchType.LAZY)
    private Image image;

    @OneToOne(fetch = FetchType.LAZY)
    private Board board;
    @OneToMany(fetch = FetchType.LAZY)
    private List<Description> descriptions;
    @OneToMany(fetch = FetchType.LAZY)
    private List<RecipeIngredient> recipeIngredients;

    public Recipe(String name, String category, String intro, String cookTime, Difficult difficult, Image image, List<Description> descriptions, List<RecipeIngredient> recipeIngredients,Board board) {
        this.name = name;
        this.category = category;
        this.intro = intro;
        this.cookTime = cookTime;
        this.difficult = difficult;
        this.image = image;
        this.descriptions = descriptions;
        this.recipeIngredients = recipeIngredients;
        this.board =board;
    }
}