package Fridge_Chef.team.comment.repository;

import Fridge_Chef.team.comment.domain.CommentUserEvent;
import Fridge_Chef.team.user.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentUserEventRepository extends JpaRepository<CommentUserEvent, Long> {
    Optional<CommentUserEvent> findByBoardIdAndCommentsIdAndUserUserId(Long boardId, Long commentId, UserId userId);
}
