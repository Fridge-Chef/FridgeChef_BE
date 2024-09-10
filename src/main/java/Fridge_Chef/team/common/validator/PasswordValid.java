package Fridge_Chef.team.common.validator;


import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
public @interface PasswordValid {

    String message() default "비밀번호: 6~38자의 영문, 숫자를 사용해주세요.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
