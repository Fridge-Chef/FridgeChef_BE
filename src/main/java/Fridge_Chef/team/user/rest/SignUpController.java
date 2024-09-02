package Fridge_Chef.team.user.rest;


import Fridge_Chef.team.cert.service.CertSignUpService;
import Fridge_Chef.team.cert.service.request.EmailVerifyRequest;
import Fridge_Chef.team.cert.service.request.SignUpCertRequest;
import Fridge_Chef.team.cert.service.request.SignUpEmailVerifyRequest;
import Fridge_Chef.team.mail.service.EmailService;
import Fridge_Chef.team.user.rest.request.SignUpEmailSendRequest;
import Fridge_Chef.team.user.rest.request.SignUpRequest;
import Fridge_Chef.team.user.rest.response.SignUpCertVerifyResponse;
import Fridge_Chef.team.user.rest.response.SignUpIdCheckResponse;
import Fridge_Chef.team.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/signup")
@RequiredArgsConstructor
public class SignUpController {
    private final CertSignUpService certSignUpService;
    private final UserService userService;
    private final EmailService emailService;

    @PostMapping
    public void signup(@Valid @RequestBody SignUpRequest request) {
        certSignUpService.validateCert(request.email());
        userService.validateMemberRegistration(request.userId(), request.email());
        userService.signup(request.password(), request.userId(), request.userName(), request.email());
        certSignUpService.deleteAuthenticationComplete(request);
    }

    @PostMapping("/idCheck")
    public SignUpIdCheckResponse idCheck(@RequestBody String id) {
        return new SignUpIdCheckResponse(userService.isIdCheck(id));
    }


    @PostMapping("/email")
    public void emailCertCodeSend(@Valid @RequestBody SignUpEmailSendRequest request) {
        userService.checkEmailValidAndUnique(request.email());
        certSignUpService.validateEmailVerificationExceed(request.email());
        int verificationCode = certSignUpService.newVerificationCode();
        emailService.signupCertSend(request.email(), verificationCode);
        certSignUpService.saveCert(new SignUpCertRequest(verificationCode, request.email()));
    }

    @PostMapping("/email/auth")
    public SignUpCertVerifyResponse emailCertVerify(@Valid @RequestBody SignUpEmailVerifyRequest verifyRequest) {
        return certSignUpService.emailVerifyCodeCheck(EmailVerifyRequest.of(verifyRequest.send(), verifyRequest.code()));
    }
}
