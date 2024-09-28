package fixture;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardType;
import Fridge_Chef.team.user.domain.User;


public class BoardFixture {

    public static Board create(User user) {
        Board board =  new Board(user,"",null,null, BoardType.USER);
        board.updateId(1L);
        return board;
    }
}
