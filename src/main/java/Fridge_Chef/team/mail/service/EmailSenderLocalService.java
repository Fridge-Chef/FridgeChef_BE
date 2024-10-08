package Fridge_Chef.team.mail.service;


import Fridge_Chef.team.config.local.EmailLocalConfig;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.mail.service.request.EmailRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;

import static Fridge_Chef.team.mail.output.EmailSignupOutput.signupText;
import static Fridge_Chef.team.mail.output.EmailSignupOutput.signupTitle;

@Slf4j
@Profile({"local"})
@Configuration
public class EmailSenderLocalService implements EmailService {

    private final String email;

    public EmailSenderLocalService(EmailLocalConfig emailConfig) {
        emailConfig.getJavaMailSender();
        this.email = "test@gmail.com";
    }

    public void sendMessage(EmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(request.to());
        message.setSubject(request.subject());
        message.setText(request.text());
        send(message);
    }

    private void send(SimpleMailMessage message) {
        log.info(message.getFrom());
        log.info(message.getSubject());
        log.info(message.getText());
    }

    @Override
    public void signupCertSend(String email, int verificationCode) {
        try {
            String title = signupTitle();
            String sendText = signupText(verificationCode);
            sendMessage(new EmailRequest(email, title, sendText));
        } catch (MailParseException e1) {
            throw new ApiException(ErrorCode.EMAIL_SEND_PARSE);
        } catch (MailAuthenticationException e2) {
            throw new ApiException(ErrorCode.EMAIL_SEND_AUTHENTICATION);
        } catch (MailSendException e3) {
            throw new ApiException(ErrorCode.EMAIL_SEND);
        }
    }
}
