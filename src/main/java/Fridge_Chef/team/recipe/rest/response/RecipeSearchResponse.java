package Fridge_Chef.team.recipe.rest.response;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.recipe.domain.RecipeIngredient;
import Fridge_Chef.team.user.domain.UserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeSearchResponse {

    private Long id;
    private String title;
    private String userName;
    private String mainImage;
    private double star;
    private int hit;
    private boolean myHit;
    private int click;
    private LocalDateTime createTime;

    private long have;
    private long withoutCount;
    private List<String> without;

    public static RecipeSearchResponse of(Board board, List<String> pick) {
        List<RecipeIngredient> ingredients = board.getContext().getBoardIngredients();
        List<String> without = ingredients.stream()
                .map(pi -> pi.getIngredient().getName())
                .filter(ingredientName -> !pick.contains(ingredientName))
                .toList();

        return new RecipeSearchResponse(board.getId(),
                board.getTitle(),
                board.getUser().getUsername(),
                board.getMainImageLink(),
                board.getTotalStar(),
                board.getHit(),
                board.getIsMyHit(UserId.create()),
                board.getCount(),
                board.getCreateTime(),
                ingredients.size() - without.size(),
                without.size(),
                without
        );
    }

}
