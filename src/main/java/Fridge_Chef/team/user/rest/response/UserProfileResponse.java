package Fridge_Chef.team.user.rest.response;

import Fridge_Chef.team.user.domain.User;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonTypeInfo.As.WRAPPER_OBJECT;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.Id.NAME;

@JsonTypeName("user")
@JsonTypeInfo(include = WRAPPER_OBJECT, use = NAME)
public record UserProfileResponse(String email, String role, String username , LocalDateTime createAt) {
    public static UserProfileResponse from(User user) {
        return new UserProfileResponse(
                user.getEmail(),
                user.getRole().getAuthority(),
                user.getProfile().getUsername(),
                user.getCreateTime()
        );
    }
}