package Fridge_Chef.team.recipe.domain;

import Fridge_Chef.team.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;


@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class RecipeDescription extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String description;
    private String uri;

    public RecipeDescription(String description, String uri) {
        this.description = description;
        this.uri = uri;
    }
}