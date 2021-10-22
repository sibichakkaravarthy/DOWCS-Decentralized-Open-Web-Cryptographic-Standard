package me.twodee.dowcspws.validator;

import com.nulabinc.zxcvbn.Strength;
import com.nulabinc.zxcvbn.Zxcvbn;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordStrengthValidator implements ConstraintValidator<PasswordStrength, String> {
    private PasswordStrength.Strength strength;
    @Override
    public void initialize(PasswordStrength constraintAnnotation) {
        strength = constraintAnnotation.minStrength();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        Zxcvbn strengthChecker = new Zxcvbn();
        if (password == null) {
            return false;
        }
        Strength strength = strengthChecker.measure(password);
        return strength.getScore() >= getMinScore();
    }

    private int getMinScore() {
        switch (strength) {
            case WEAK:
                return 0;
            case FAIR:
                return 1;
            case GOOD:
                return 2;
            case STRONG:
                return 3;
            case VERY_STRONG:
                return 4;
        }
        return 2;
    }
}
