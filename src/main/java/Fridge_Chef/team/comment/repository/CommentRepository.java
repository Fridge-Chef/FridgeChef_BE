package Fridge_Chef.team.comment.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.comment.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByBoard(Board board);
}
