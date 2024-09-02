package Fridge_Chef.team.mail.service;


import Fridge_Chef.team.mail.service.request.EmailRequest;

public interface EmailRepository {
    void sendMessage(EmailRequest emailRequest);
}
