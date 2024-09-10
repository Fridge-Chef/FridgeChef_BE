package Fridge_Chef.team.cert.rest.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record SignUpEmailSendRequest(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotNull(message = "이메일: 필수 정보입니다.")
        String email) {
}
