package Fridge_Chef.team.recipe.rest.response;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardUserEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Arrays;
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

    public static RecipeSearchResponse of(Board board, List<String> pick, List<BoardUserEvent> boardUserEvents) {
        List<String> ingredients = Arrays.stream(board.getContext().getPathIngredient().split(","))
                .toList();

        List<String> without = ingredients.stream()
                .filter(ingredientName -> !pick.contains(ingredientName))
                .toList();

        boolean isUserHit = false;

        var events = boardUserEvents.stream()
                .filter(event -> event.getBoard().getId().equals(board.getId()))
                .findFirst();
        if(events.isPresent()){
            isUserHit = events.get().isUserHit();
        }

        return new RecipeSearchResponse(board.getId(),
                board.getTitle(),
                board.getUser().getUsername(),
                board.getPathMainImage(),
                board.getTotalStar(),
                board.getHit(),
                isUserHit,
                board.getCount(),
                board.getCreateTime(),
                ingredients.size() - without.size(),
                without.size(),
                without
        );
    }

    public static List<RecipeSearchResponse> of(List<Board> board, List<String> pick, List<BoardUserEvent> boardUserEvents) {
        return board.stream()
                .map(v -> RecipeSearchResponse.of(v, pick,boardUserEvents))
                .toList();
    }

    public static List<RecipeSearchResponse> of(List<Board> board, List<String> pick,boolean ignore) {
        return board.stream()
                .map(v -> RecipeSearchResponse.of(v, pick,List.of()))
                .toList();
    }
}
