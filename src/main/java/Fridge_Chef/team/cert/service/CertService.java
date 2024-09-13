package Fridge_Chef.team.cert.service;

import Fridge_Chef.team.cert.domain.Cert;
import Fridge_Chef.team.cert.repository.CertRepository;
import Fridge_Chef.team.cert.service.request.EmailVerifyRequest;
import Fridge_Chef.team.cert.service.request.SignUpCertRequest;
import Fridge_Chef.team.common.entity.OracleBoolean;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.mail.rest.request.SignUpRequest;
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
        Optional<Cert> cert = repository.findFirstByEmailOrderByCreateTimeDesc(email);
        if (cert.isEmpty()) {
            throw new ApiException(ErrorCode.SIGNUP_CERT_NON_REQUEST);
        }
        if (!cert.get().getAuthentication().bool()) {
            throw new ApiException(ErrorCode.SIGNUP_CERT_CODE_UNVERIFIED);
        }
    }

    @Transactional
    public void emailVerifyCodeCheck(EmailVerifyRequest request) {
        Cert cert = verifyCodeCheck(request)
                .orElseThrow(() -> new ApiException(ErrorCode.SIGNUP_EMAIL_VERIFY_CODE_FAILED));
        cert.updateAuthentication(true);
    }

    public int newVerificationCode() {
        Random random = new Random();

        return 100000 + random.nextInt(900000);
    }

    @Transactional
    public void deleteAuthenticationComplete(SignUpRequest request) {
        List<Cert> certs = repository.findByEmailAndAuthentication(request.email(), OracleBoolean.T)
                .orElse(Collections.emptyList());
        repository.deleteAll(certs);
    }

    private Optional<Cert> verifyCodeCheck(EmailVerifyRequest request) {
        return repository.findFirstByEmailAndVerificationCodeOrderByCreateTimeDesc(request.email(), request.code());
    }
}
