package Fridge_Chef.team.user.repository;


import Fridge_Chef.team.common.entity.OracleBoolean;
import Fridge_Chef.team.user.domain.Social;
import Fridge_Chef.team.user.domain.User;
import Fridge_Chef.team.user.domain.UserId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUserId_Value(UUID userId);

    Optional<User> findByProfileEmailAndProfileSocial(String email, Social social);

    boolean existsByProfileEmailAndProfileSocial(String email, Social social);

    Optional<User> findByUserId(UserId userid);

    List<User> findByDeleteStatus(OracleBoolean deleteStatus);

    boolean existsByProfileEmail(String email);

}