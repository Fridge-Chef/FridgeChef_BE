package Fridge_Chef.team.mail.service;


import Fridge_Chef.team.mail.service.request.EmailRequest;

public interface EmailService {
    void sendMessage(EmailRequest emailRequest);

    void signupCertSend(String email, int verificationCode);

    void certSend(String email, int verificationCode);
}
