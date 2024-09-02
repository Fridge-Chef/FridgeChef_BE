package Fridge_Chef.team.user.repository;


import Fridge_Chef.team.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId(String userId);
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);
}