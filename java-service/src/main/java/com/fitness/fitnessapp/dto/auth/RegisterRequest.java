package com.fitness.fitnessapp.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Should contains from 3 to 50 symbols")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid format of email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Minimum 6 symbols")
    private String password;

    @NotBlank(message = "Confirm password")
    private String confirmPassword;
}