package me.twodee.dowcspws.validator;


import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordStrengthValidator.class)
@Target( { ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface PasswordStrength {
    public enum Strength {
        WEAK,
        FAIR,
        GOOD,
        STRONG,
        VERY_STRONG
    }
    String message() default "The password is too weak";
    Strength minStrength() default Strength.FAIR;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}