package Fridge_Chef.team.security.service.factory;


import Fridge_Chef.team.security.service.dto.OAuthAttributes;

import java.util.Map;

public interface OAuthAttributesAdapter {
    OAuthAttributes toOAuthAttributes(Map<String, Object> attributes);

    boolean supports(String registrationId);
}