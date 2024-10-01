package Fridge_chef.team.mail;

import Fridge_Chef.team.mail.service.EmailService;
import Fridge_Chef.team.mail.service.request.EmailRequest;
import Fridge_chef.team.FridgeChefApplicationApiTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

@DisplayName("mail 테스트")
public class MailTest extends FridgeChefApplicationApiTest {
    @Autowired
    private EmailService emailService;

    @Value("${spring.mail.username}")
    private String testEmail;

    @Test
    @DisplayName("전송 성공")
    void send(){
        Assertions.assertDoesNotThrow(() -> emailService.sendMessage(new EmailRequest(testEmail, "test 용 이메일", "test")));
    }
}
