package Fridge_Chef.team.comment.repository;

import Fridge_Chef.team.comment.domain.CommentUserEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentUserEventRepository extends JpaRepository<CommentUserEvent,Long> {
}
