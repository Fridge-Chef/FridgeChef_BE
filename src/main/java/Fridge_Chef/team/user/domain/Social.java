package Fridge_Chef.team.user.domain;

import Fridge_Chef.team.exception.ApiException;
import Fridge_Chef.team.exception.ErrorCode;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Arrays;

@Getter
@Accessors(fluent = true)
public enum Social {
    KAKAO("카카오"),
    GOOGLE("구글");
    private final String title;

    Social(String title) {
        this.title = title;
    }

    public static Social signupOf(String src) {
        return Arrays.stream(values())
                .filter(social -> social.name().equalsIgnoreCase(src))
                .findFirst()
                .orElseThrow(() -> new ApiException(ErrorCode.SIGNUP_SNS_NOT_SUPPORT));
    }
}