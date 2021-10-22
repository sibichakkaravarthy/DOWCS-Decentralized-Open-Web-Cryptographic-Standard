package me.twodee.dowcspws.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.NotBlank;

public class Ttp {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    public static class Registration {
        @NotBlank(message = "All fields must be filled")
        private String name;

        @NotBlank(message = "All fields must be filled")
        @URL(message = "The base url isn't valid")
        private String baseUrl;

        @NotBlank(message = "All fields must be filled")
        private String pwsId;

        @NotBlank(message = "All fields must be filled")
        private String challenge;
    }
}
