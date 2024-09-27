package Fridge_Chef.team.board.repository;

import Fridge_Chef.team.board.domain.Description;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DescriptionRepository extends JpaRepository<Description,Long> {
}
