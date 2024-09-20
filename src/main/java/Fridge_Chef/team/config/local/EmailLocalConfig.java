package Fridge_Chef.team.config.local;

import Fridge_Chef.team.config.EmailConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Profile("local")
@Configuration
public class EmailLocalConfig implements EmailConfig {
    private String host;
    private int port;
    private String username;
    private String password;
    private String protocol;
    private boolean auth;
    private boolean starttls;

    public JavaMailSender getJavaMailSender() {
        return new JavaMailSenderImpl();
    }
}
