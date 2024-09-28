package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardUserEvent;
import Fridge_Chef.team.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardUserEventRepository extends JpaRepository<BoardUserEvent, Long> {
    List<BoardUserEvent> findByBoard(Board board);

    List<BoardUserEvent> findByBoardAndUser(Board board, User user);
}
