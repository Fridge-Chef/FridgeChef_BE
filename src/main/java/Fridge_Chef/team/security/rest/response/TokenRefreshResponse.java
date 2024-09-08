package Fridge_Chef.team.security.rest.response;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@Getter
@JsonTypeName("user")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
@AllArgsConstructor
public class TokenRefreshResponse {
    private final String refreshToken;

    public static TokenRefreshResponse from(String token) {
        return new TokenRefreshResponse(token);
    }
}
