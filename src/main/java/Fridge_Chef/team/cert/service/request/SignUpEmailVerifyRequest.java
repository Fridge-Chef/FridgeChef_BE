package Fridge_Chef.team.cert.service.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignUpEmailVerifyRequest(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotNull(message = "이메일: 필수 정보입니다.")
        String send,

        @Pattern(regexp = "^[0-9]{6}$", message = "인증번호는ㄴ 6자리 숫자여야 합니다.")
        @NotNull(message = "인증번호를 입력해주세요.")
        int code) {
}
