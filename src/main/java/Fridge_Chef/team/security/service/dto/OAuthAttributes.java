package Fridge_Chef.team.security.service.dto;

import lombok.Builder;

import java.util.Map;


@Builder
public record OAuthAttributes(
        Map<String, Object> attributes,
        String nameAttributeKey,
        String name,
        String email,
        String picture,
        String registrationId
) {
}
