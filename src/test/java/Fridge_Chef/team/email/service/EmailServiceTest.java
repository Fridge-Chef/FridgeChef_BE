package Fridge_Chef.team.email.service;

import Fridge_Chef.team.mail.service.EmailService;
import Fridge_Chef.team.mail.service.request.EmailRequest;
import org.junit.jupiter.api.Test;
public class EmailServiceTest {

    private EmailService emailService = new EmailSenderServiceTest();

    @Test
    void send() {
        EmailRequest request = new EmailRequest(
                "jeonghun.kang.dev@gmail.com",
                "test title",
                "hi"
        );
        emailService.sendMessage(request);
    }

}
