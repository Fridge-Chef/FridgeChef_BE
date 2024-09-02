package Fridge_Chef.team.cert.repository;

import Fridge_Chef.team.cert.domain.Cert;
import Fridge_Chef.team.common.entity.OracleBoolean;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static Fridge_Chef.team.cert.domain.QCert.cert;


@Repository
@RequiredArgsConstructor
public class CertDslRepository {
    private final JPAQueryFactory factory;

    public Optional<Cert> findByEmailAndVerificationCode(String email, int verificationCode) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        return Optional.ofNullable(factory.selectFrom(cert)
                .where(cert.email.eq(email)
                        .and(cert.verificationCode.eq(verificationCode))
                        .and(cert.createTime.after(oneDayAgo))
                )
                .orderBy(cert.createTime.desc())
                .fetchFirst());
    }

    public int countByVerificationSent(String verificationSent) {
        LocalDateTime oneDayAgo = LocalDateTime.now().minusDays(1);
        return factory.selectFrom(cert)
                .where(cert.email.eq(verificationSent)
                        .and(cert.createTime.after(oneDayAgo))
                )
                .fetch().size();
    }

    @Transactional
    public void updateByEmail( OracleBoolean authentication, String email){
        factory.update(cert)
                .set(cert.authentication,authentication)
                .where(cert.email.eq(email))
                .execute();
    }
}
