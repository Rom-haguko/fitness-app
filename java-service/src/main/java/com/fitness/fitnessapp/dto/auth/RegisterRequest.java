package com.fitness.fitnessapp.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest (
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Should contains from 3 to 50 symbols")
    String username,

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid format of email")
    String email,

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Minimum 6 symbols")
    String password,

    @NotBlank(message = "Confirm password")
    String confirmPassword
){}