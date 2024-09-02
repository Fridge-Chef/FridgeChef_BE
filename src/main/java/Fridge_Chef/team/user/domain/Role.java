package Fridge_Chef.team.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
@Accessors(fluent = true)
public enum Role implements GrantedAuthority {

    USER("일반 사용자", "ROLE_USER"),
    ADMIN("관리자", "ROLE_ADMIN");

    private final String title;
    private final String value;

    public static Role of(String value) {
        return Arrays.stream(values())
                .filter(role -> role.value.equals(value) ||role.value.equals("ROLE_"+value)  )
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 권한입니다."));
    }

    @Override
    public String getAuthority() {
        return this.value;
    }
}