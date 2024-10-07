package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoardHistoryRepository extends JpaRepository<BoardHistory,Long> {
}
