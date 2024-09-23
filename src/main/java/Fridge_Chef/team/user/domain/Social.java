package Fridge_Chef.team.user.domain;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Arrays;

@Accessors(fluent = true)
@RequiredArgsConstructor
public enum Social {
    KAKAO("카카오"),
    GOOGLE("구글");

    private final String title;

    public static Social of(String value) {
        return Arrays.stream(values())
                .filter(social -> social.name().equalsIgnoreCase(value)) // 대소문자 구분 없이 비교
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid social provider: " + value));
    }
}