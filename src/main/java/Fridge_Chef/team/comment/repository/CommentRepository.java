package Fridge_Chef.team.comment.repository;

import Fridge_Chef.team.board.domain.Board;
import Fridge_Chef.team.comment.domain.Comment;
import Fridge_Chef.team.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findAllByBoard(Board board);

    Optional<List<Comment>> findByUsers(User user);

}
