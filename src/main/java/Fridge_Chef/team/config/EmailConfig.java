package Fridge_Chef.team.config;

import org.springframework.mail.javamail.JavaMailSender;

public interface EmailConfig {
    JavaMailSender getJavaMailSender();
}
