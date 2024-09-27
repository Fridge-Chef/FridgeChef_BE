package Fridge_Chef.team.board.rest.request;

import Fridge_Chef.team.board.repository.model.SortType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardPageRequest {
    @Size(min=1)
    private int page;
    @Size(min=1,max=50,message = "1~50 사이즈 이상 값을 넣을 수 없습니다.")
    private int size;
    private SortType sortType;
}
