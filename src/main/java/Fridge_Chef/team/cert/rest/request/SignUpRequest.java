package Fridge_Chef.team.cert.rest.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @Pattern(regexp = "^[a-zA-Z0-9_-]{4,20}$", message = "아이디: 4~20자의 영문,숫자와 특수기호(_),(-)만 사용 가능합니다.")
        @NotNull(message = "아이디: 필수 정보입니다.")
        String userId,
        @Size(min=2,max=30 ,message = "이름:2~30자")
        @NotNull(message = "이름: 필수 정보입니다")
        String userName,
        @Pattern(regexp = "^[a-zA-Z0-9]{8,16}$", message = "비밀번호: 8~16자의 영문,숫자를 사용해주세요.")
        @NotNull(message = "비밀번호: 필수 정보입니다.")
        String password,
        String email
) {
}
