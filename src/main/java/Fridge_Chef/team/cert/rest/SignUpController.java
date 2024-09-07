package Fridge_Chef.team.cert.rest;


import Fridge_Chef.team.cert.service.CertService;
import Fridge_Chef.team.cert.service.request.EmailVerifyRequest;
import Fridge_Chef.team.cert.service.request.SignUpCertRequest;
import Fridge_Chef.team.cert.service.request.SignUpEmailVerifyRequest;
import Fridge_Chef.team.mail.service.EmailService;
import Fridge_Chef.team.cert.rest.request.SignUpEmailSendRequest;
import Fridge_Chef.team.cert.rest.request.SignUpRequest;
import Fridge_Chef.team.cert.rest.response.SignUpCertVerifyResponse;
import Fridge_Chef.team.cert.rest.response.SignUpIdCheckResponse;
import Fridge_Chef.team.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/signup")
@RequiredArgsConstructor
public class SignUpController {
    private final CertService certService;
    private final UserService userService;
    private final EmailService emailService;

    @PostMapping
    public void signup(@Valid @RequestBody SignUpRequest request) {
        certService.validateCert(request.email());
        userService.validateMemberRegistration(request.userId(), request.email());
        userService.signup(request.email(),request.password(), request.userName());
        certService.deleteAuthenticationComplete(request);
    }

    @PostMapping("/idCheck")
    public SignUpIdCheckResponse idCheck(@RequestBody String id) {
        return new SignUpIdCheckResponse(userService.isIdCheck(id));
    }


    @PostMapping("/email/send")
    public void emailCertCodeSend(@Valid @RequestBody SignUpEmailSendRequest request) {
        userService.checkEmailValidAndUnique(request.email());
        certService.validateEmailVerificationExceed(request.email());
        int verificationCode = certService.newVerificationCode();
        emailService.signupCertSend(request.email(), verificationCode);
        certService.saveCert(new SignUpCertRequest(verificationCode, request.email()));
    }

    @PostMapping ("/email/verify")
    public SignUpCertVerifyResponse emailCertVerify(@Valid @RequestBody SignUpEmailVerifyRequest verifyRequest) {
        return certService.emailVerifyCodeCheck(EmailVerifyRequest.of(verifyRequest.send(), verifyRequest.code()));
    }
}
