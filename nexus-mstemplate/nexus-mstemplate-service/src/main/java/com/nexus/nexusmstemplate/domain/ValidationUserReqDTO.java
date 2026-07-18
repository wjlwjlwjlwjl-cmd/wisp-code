package com.nexus.nexusmstemplate.domain;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ValidationUserReqDTO {
    @NotNull(message = "name should not be empty")
    private String name;

    @NotNull(message = "userAccount should not be empty")
    private String userAccount;

    @NotNull(message = "password should not be empty")
    @Size(min = 6, max = 10, message = "the length of the password should within 6 and 10")
    private String password;

    @Min(value = 6, message = "user should older than six")
    private int age;

    @Pattern(regexp = "^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\\d{8}$", message = "the format of the give phone number is incorrect")
    private String phone;

    @Email
    private String email;

    @Past(message = "startDate should be a passed Date")
    private LocalDate startDate;

    @Future(message = "endDate should be an uncoming Date")
    private LocalDate endDate;
}