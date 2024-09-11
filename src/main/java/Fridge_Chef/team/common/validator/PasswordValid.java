package Fridge_Chef.team.common.validator;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordValid {

    String message() default "비밀번호: 6~38자의 영문, 숫자를 사용해주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
