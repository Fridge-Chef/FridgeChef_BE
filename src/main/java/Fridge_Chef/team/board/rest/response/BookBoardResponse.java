package Fridge_Chef.team.board.rest.response;

import Fridge_Chef.team.board.domain.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class BookBoardResponse {
    private Long id;
    private String mainImageLink;
    private String title;
    private double star;
    private int hit;

    public BookBoardResponse(Board entity) {
        this.id=entity.getId();
        this.mainImageLink=entity.getMainImageLink();
        this.title=entity.getTitle();
        this.star=entity.getTotalStar();
        this.hit=entity.getHit();
    }
}
