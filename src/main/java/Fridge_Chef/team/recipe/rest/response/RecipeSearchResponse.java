package Fridge_Chef.team.recipe.rest.response;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.QBoard;
import Fridge_Chef.team.user.domain.UserId;
import com.querydsl.core.Tuple;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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

    public static RecipeSearchResponse of(Board board, List<String> pick, Optional<UserId> userId) {
        List<String> ingredients = Arrays.stream(board.getContext().getPathIngredient().split(","))
                .toList();

        List<String> without = ingredients.stream()
                .filter(ingredientName -> !pick.contains(ingredientName))
                .toList();

        return new RecipeSearchResponse(board.getId(),
                board.getTitle(),
                board.getUser().getUsername(),
                board.getPathMainImage(),
                board.getTotalStar(),
                board.getHit(),
                userId.isEmpty() ? false : board.getIsMyHit(userId.get()),
                board.getCount(),
                board.getCreateTime(),
                ingredients.size() - without.size(),
                without.size(),
                without
        );
    }

    public static RecipeSearchResponse of(Tuple tuple, List<String> pick, Optional<UserId> userId) {
        QBoard board = QBoard.board;
        System.out.println("" + tuple.toString());
        return RecipeSearchResponse.of(Objects.requireNonNull(tuple.get(board)), pick, userId);
    }
}
