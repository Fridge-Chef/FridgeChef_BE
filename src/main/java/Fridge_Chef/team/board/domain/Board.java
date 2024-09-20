package Fridge_Chef.team.board.domain;

import Fridge_Chef.team.category.domain.Category;
import Fridge_Chef.team.common.entity.BaseEntity;
import Fridge_Chef.team.common.entity.Star;
import Fridge_Chef.team.image.domain.Image;
import Fridge_Chef.team.recipe.domain.Recipe;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Table
@NoArgsConstructor(access = PROTECTED)
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    private String title;
    @ManyToOne(fetch = FetchType.LAZY)
    private Category category;
    @ManyToOne(fetch = FetchType.LAZY)
    private Context context;
    @Enumerated(EnumType.STRING)
    private BoardType type;
    @ManyToOne
    private Image mainImage;
    private Star star;
    private int hit;
    private int count;

    public Board(String title, Category category, Context context, Image mainImage, BoardType type) {
        this.title = title;
        this.category = category;
        this.context = context;
        this.mainImage = mainImage;
        this.type = type;
        this.star = Star.ONE;
        this.hit = 0;
        this.count = 0;
    }

    private Board(String title, Category category, Context context, Image mainImage) {
        this.title = title;
        this.category = category;
        this.context = context;
        this.mainImage = mainImage;
        this.type = BoardType.OPEN_API;
        this.star = Star.ONE;
        this.hit = 0;
        this.count = 0;
    }

    public static Board fromRecipe(Recipe recipe) {
        return new Board(recipe.getName(),
                new Category(1L),
                Context.fromRecipe(recipe),
                Image.outUri(recipe.getImageUrl())
        );
    }
}
