package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardUserEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardUserEventRepository extends JpaRepository<BoardUserEvent,Long> {
    List<BoardUserEvent> findByBoard(Board board);
}
