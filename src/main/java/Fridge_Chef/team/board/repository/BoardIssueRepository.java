package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.board.domain.BoardIssue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface BoardIssueRepository extends JpaRepository<BoardIssue,Long> {
    boolean existsByBoardAndCreateTimeBetween(Board board, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
