package Fridge_Chef.team.cert.repository;

import Fridge_Chef.team.cert.domain.Cert;
import Fridge_Chef.team.common.entity.OracleBoolean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CertRepository extends JpaRepository<Cert, Long> {

    Optional<List<Cert>> findByEmailAndAuthentication( String email, OracleBoolean oracleBoolean);
    Optional<Cert> findFirstByEmailAndVerificationCodeOrderByCreateTimeDesc(String email,int verificationCode);
    Optional<Cert> findFirstByEmailOrderByCreateTimeDesc(String email);
}
