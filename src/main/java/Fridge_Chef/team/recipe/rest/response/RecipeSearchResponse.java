package Fridge_Chef.team.recipe.rest.response;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardUserEvent;
import Fridge_Chef.team.user.domain.UserId;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Data
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
    private boolean myMe;
    private int click;
    private LocalDateTime createTime;

    private long have;
    private long withoutCount;
    private List<String> without;


    private static RecipeSearchResponse of(Board board, List<String> pick, List<BoardUserEvent> boardUserEvents, Optional<UserId> user) {
        List<String> ingredients = Arrays.stream(board.getContext().getPathIngredient().split(","))
                .toList();

        List<String> without = ingredients.stream()
                .filter(ingredientName -> !pick.contains(ingredientName))
                .toList();

        boolean isUserHit = false;
        boolean isMyMe = false;
        if (user.isPresent()) {
            if (board.getUser().getUserId().equals(user.get())) {
                isMyMe = true;
            }
        }
        var events = boardUserEvents.stream()
                .filter(event -> event.getBoard().getId().equals(board.getId()))
                .findFirst();
        if (events.isPresent()) {
            isUserHit = events.get().isUserHit();
        }

        return new RecipeSearchResponse(board.getId(),
                board.getTitle(),
                board.getUser().getUsername(),
                board.getPathMainImage(),
                board.getTotalStar(),
                board.getHit(),
                isUserHit,
                isMyMe,
                board.getCount(),
                board.getCreateTime(),
                ingredients.size() - without.size(),
                without.size(),
                without
        );
    }

    public static List<RecipeSearchResponse> of(List<Board> board, List<String> pick, List<BoardUserEvent> boardUserEvents, Optional<UserId> userId) {
        return board.stream()
                .map(v -> RecipeSearchResponse.of(v, pick, boardUserEvents, userId))
                .toList();
    }
}
