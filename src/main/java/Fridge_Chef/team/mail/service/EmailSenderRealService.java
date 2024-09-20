package Fridge_Chef.team.mail.service;

import Fridge_Chef.team.config.prod.EmailProdConfig;
import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import Fridge_Chef.team.mail.service.request.EmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailParseException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static Fridge_Chef.team.mail.output.EmailSignupOutput.signupText;
import static Fridge_Chef.team.mail.output.EmailSignupOutput.signupTitle;

@Configuration
@Profile({"prod", "dev"})
public class EmailSenderRealService implements EmailService {
    private final JavaMailSender emailConfig;

    private final String email;

    public EmailSenderRealService(EmailProdConfig emailConfig,
                                  @Value("${spring.mail.username}") String email) {
        this.emailConfig = emailConfig.getJavaMailSender();
        this.email = email;
    }

    public void sendMessage(EmailRequest request) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(email);
        message.setTo(request.to());
        message.setSubject(request.subject());
        message.setText(request.text());
        send(message);
    }

    private void send(SimpleMailMessage message){
        emailConfig.send(message);
    }

    @Override
    public void signupCertSend(String email, int verificationCode) {
        try {
            String title = signupTitle();
            String sendText = signupText(verificationCode);
            sendMessage(new EmailRequest(email, title, sendText));
        }catch (MailParseException e1){
            throw new ApiException(ErrorCode.EMAIL_SEND_PARSE);
        }catch (MailAuthenticationException e2){
            throw new ApiException(ErrorCode.EMAIL_SEND_AUTHENTICATION);
        }catch (MailSendException e3){
            throw new ApiException(ErrorCode.EMAIL_SEND);
        }
    }
}
