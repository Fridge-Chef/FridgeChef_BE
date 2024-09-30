package Fridge_Chef.team.security.service.factory.adapter;

import org.springframework.security.oauth2.client.registration.ClientRegistration;

public interface GoogleOauthAttribute {
    ClientRegistration getGoogleClientRegistration();

    boolean supports(String registrationId);
}
