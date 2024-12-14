package Fridge_Chef.team.security.service.dto;

import lombok.Data;

@Data
public class GoogleTokenDTO {
    private String iss;
    private String azp;
    private String aud;
    private String sub;
    private String email;
    private boolean emailVerified;
    private String name;
    private String picture;
    private String givenName;
    private String familyName;
    private long iat;
    private long exp;
    private String alg;
    private String kid;
    private String typ;

    public OAuthAttributes toOAuthAttributes() {
        return OAuthAttributes.builder()
                .registrationId("google")
                .email(email)
                .picture(picture)
                .name(name)
                .nameAttributeKey(kid)
                .build();
    }
}
