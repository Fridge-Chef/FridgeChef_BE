package Fridge_Chef.team.cert.rest.request;

import Fridge_Chef.team.common.validator.PasswordValid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SignUpRequest(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotNull(message = "이메일: 필수 정보입니다.")
        String email,
        @PasswordValid
        String password,

        @Size(min=2,max=30 ,message = "이름:2~30자")
        @NotNull(message = "이름: 필수 정보입니다")
        String username
) {
}
