package Fridge_Chef.team.mail.rest;


import Fridge_Chef.team.cert.service.CertService;
import Fridge_Chef.team.cert.service.request.EmailVerifyRequest;
import Fridge_Chef.team.cert.service.request.SignUpCertRequest;
import Fridge_Chef.team.cert.service.request.SignUpEmailVerifyRequest;
import Fridge_Chef.team.mail.rest.request.SignUpEmailSendRequest;
import Fridge_Chef.team.mail.service.EmailService;
import Fridge_Chef.team.user.rest.request.UserEmailCheckRequest;
import Fridge_Chef.team.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
public class EmailSignupController {
    private final CertService certService;
    private final UserService userService;
    private final EmailService emailService;


    @PostMapping("/check")
    public void emailCheck(@RequestBody UserEmailCheckRequest request) {
        userService.checkEmailValidAndUnique(request.email());
    }

    @PostMapping("/send")
    public void emailCertCodeSend(@Valid @RequestBody SignUpEmailSendRequest request) {
        userService.checkEmailValidAndUnique(request.email());
        int verificationCode = certService.newVerificationCode();
        emailService.signupCertSend(request.email(), verificationCode);
        certService.saveCert(new SignUpCertRequest(verificationCode, request.email()));
    }

    @PostMapping("/verify")
    public void emailCertVerify(@Valid @RequestBody SignUpEmailVerifyRequest verifyRequest) {
        certService.emailVerifyCodeCheck(EmailVerifyRequest.of(verifyRequest.email(), verifyRequest.code()));
    }
}
