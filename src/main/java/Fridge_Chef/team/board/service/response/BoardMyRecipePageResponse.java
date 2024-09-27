package Fridge_Chef.team.board.service.response;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.repository.model.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class BoardMyRecipePageResponse {
    private SortType sortType;
    private Long boardId;
    private String title;
    private String userName;
    private double star;
    private int hit;
    private int click;
    private LocalDateTime createTime;

    public static BoardMyRecipePageResponse ofEntity(SortType sortType,Board entity) {
        return new BoardMyRecipePageResponse(
                sortType,
                entity.getId(),
                entity.getTitle(),
                entity.getUser().getUsername(),
                entity.getTotalStar(),
                entity.getHit(),
                entity.getCount(),
                entity.getCreateTime()
        );
    }
}
