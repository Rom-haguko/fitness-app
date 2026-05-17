package com.fitness.fitnessapp.dto.auth;

import lombok.Data;

@Data
public class UserResponse {
    private Long id;
    private String username;
    private String email;
}
