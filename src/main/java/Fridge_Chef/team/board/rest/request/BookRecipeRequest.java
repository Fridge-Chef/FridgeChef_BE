package Fridge_Chef.team.board.rest.request;

import Fridge_Chef.team.board.repository.model.BookType;
import Fridge_Chef.team.board.repository.model.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookRecipeRequest {
    private int page;
    private int size;
    private BookType bookType;
    private SortType sortType;
}
