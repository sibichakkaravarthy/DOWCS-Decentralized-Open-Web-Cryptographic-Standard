package me.twodee.dowcsttp.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class Pws {
    @Data
    public static class Registration {
        @NotBlank(message = "Name must not be blank")
        @Size(min = 4, max = 32, message = "Name must be between 4 to 32 characters")
        public String name;

        @NotBlank(message = "Description must not be blank")
        @Size(min = 10, max = 250, message = "Description must be between 10 to 250 characters")
        public String description;

        @URL(message = "Base URL is invalid")
        public String baseUrl;

        @NotBlank(message = "Callback URI must not be blank")
        public String callback;

        @NotBlank(message = "Public key must not be blank")
        public String pubkey;
    }

    @Data
    @AllArgsConstructor
    public static class Challenge {
        public String value;
        public String url;
        public String id;
    }

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ChallengeResult {
        public String challenge;
    }
}
