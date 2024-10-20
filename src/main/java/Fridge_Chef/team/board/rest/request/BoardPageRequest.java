package Fridge_Chef.team.board.rest.request;

import Fridge_Chef.team.board.repository.model.IssueType;
import Fridge_Chef.team.board.repository.model.SortType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardPageRequest {
    private int page;
    private int size;
    private IssueType issueType;
    private SortType sortType;
}
