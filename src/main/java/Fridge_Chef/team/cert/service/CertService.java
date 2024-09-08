package Fridge_Chef.team.cert.service;

import Fridge_Chef.team.common.entity.OracleBoolean;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.cert.domain.Cert;
import Fridge_Chef.team.cert.repository.CertRepository;
import Fridge_Chef.team.cert.rest.request.SignUpRequest;
import Fridge_Chef.team.cert.rest.response.SignUpCertVerifyResponse;
import Fridge_Chef.team.cert.service.request.EmailVerifyRequest;
import Fridge_Chef.team.cert.service.request.SignUpCertRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CertService {
    private final CertRepository repository;

    @Transactional
    public void saveCert(SignUpCertRequest request) {
        Cert cert = new Cert(request.verificationCode(), request.verificationSent(), OracleBoolean.F);
        repository.save(cert);
    }

    @Transactional(readOnly = true)
    public void validateCert(String email) {
        boolean isAuthentication = repository.findByEmailAndAuthentication(email, OracleBoolean.T).isPresent();

        if (!isAuthentication) {
            throw new ApiException(ErrorCode.SIGNUP_CERT_CODE_UNVERIFIED);
        }
    }

    public SignUpCertVerifyResponse emailVerifyCodeCheck(EmailVerifyRequest request) {
        verifyCodeCheck(request)
                .orElseThrow(() -> new ApiException(ErrorCode.SIGNUP_EMAIL_VERIFY_CODE_FAILED));
        updateSignCert(request.email());
        return new SignUpCertVerifyResponse(true);
    }

    public int newVerificationCode() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }


    @Transactional(readOnly = true)
    public void validateEmailVerificationExceed(String email) {
        int count = 1;
        //dslRepository.countByVerificationSent(email);

        if (count >= 10) {
            throw new ApiException(ErrorCode.SIGNUP_EMAIL_EXCEED);
        }
    }


    @Transactional
    public void deleteAuthenticationComplete(SignUpRequest request) {
        List<Cert> certs = repository.findByEmailAndAuthentication(request.email(), OracleBoolean.T)
                .orElse(Collections.emptyList());
        repository.deleteAll(certs);
    }


    @Transactional
    public void updateSignCert(String sent) {
//       dslRepository.countByVerificationSent(sent);
    }

    @Transactional(readOnly = true)
    public Optional<Cert> verifyCodeCheck(EmailVerifyRequest request) {
        return null;
//        return dslRepository.findByEmailAndVerificationCode(request.email(), request.code());
    }
}
