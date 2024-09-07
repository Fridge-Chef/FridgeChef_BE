package Fridge_Chef.team.user.rest.response;

import Fridge_Chef.team.user.domain.User;
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
public record UserResponse(String email, String token, String username) {
    public static UserResponse from(User user, String token) {
        return new UserResponse(
                user.getEmail(),
                token,
                user.getProfile().getUsername()
        );
    }
}
