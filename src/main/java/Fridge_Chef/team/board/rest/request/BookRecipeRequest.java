package Fridge_Chef.team.board.rest.request;

import Fridge_Chef.team.board.repository.model.BookType;
import Fridge_Chef.team.board.repository.model.SortType;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookRecipeRequest {
    private BookType bookType;
    private SortType sortType;
    private int page;
    @Min(1)
    @Max(50)
    private int size = 50;
}
