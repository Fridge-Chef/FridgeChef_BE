package Fridge_Chef.team.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<PasswordValid, String> {

    private static final String PASSWORD_PATTERN = "^[a-zA-Z0-9]{6,38}$";


    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }
        if (!password.matches(PASSWORD_PATTERN)) {
            return false;
        }
        return true;
    }

}