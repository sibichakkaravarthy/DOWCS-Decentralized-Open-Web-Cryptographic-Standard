package me.twodee.dowcsttp.model.dto;

import lombok.Data;
import lombok.ToString;
import me.twodee.dowcsttp.validator.PasswordStrength;

import javax.validation.constraints.*;

public class User {
    @Data
    public static class RegistrationData {

        @NotBlank(message ="The email must not be empty")
        @Email(message = "The email you provided is invalid")
        public String email;

        @NotBlank(message = "The name must not be empty")
        @Size(min = 3, max = 100, message = "The name must be between 3 to 100 characters")
        public String name;

        @Size(min = 8, message = "The password should be a minimum of 8 characters")
        @PasswordStrength(minStrength = PasswordStrength.Strength.GOOD, message = "The password you provided is too weak")
        public String password;
    }

    @Data
    public static class LoginData {
        @NotBlank(message = "The identifier must not be empty")
        public String identifier;

        @NotBlank(message = "The password is invalid")
        @Size(min = 8, message = "The password is invalid")
        public String password;

        public String csrf;
    }

    @Data
    public static class PwsConnectAuthData extends LoginData {
        @NotBlank(message = "PWS identifier is missing")
        public String special;
    }
}
