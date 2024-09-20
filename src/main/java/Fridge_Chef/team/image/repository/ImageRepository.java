package Fridge_Chef.team.image.repository;

import Fridge_Chef.team.image.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Image, Long> {
}
