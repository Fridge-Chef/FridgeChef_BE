package Fridge_Chef.team.cert.service.request;

import jakarta.validation.constraints.*;

public record SignUpEmailVerifyRequest(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotNull(message = "이메일: 필수 정보입니다.")
        String email,

        @Min(value = 100000, message = "인증번호는 6자리 숫자여야 합니다.")
        @Max(value = 999999, message = "인증번호는 6자리 숫자여야 합니다.")
        int code) {
}
