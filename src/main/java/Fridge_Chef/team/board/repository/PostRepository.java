package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
