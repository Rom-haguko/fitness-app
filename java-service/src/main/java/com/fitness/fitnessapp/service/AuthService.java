package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.auth.RegisterRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;

    @Transactional
    public void register(RegisterRequest request){
        if (userService.existsByUsername(request.getUsername())){
            throw new RuntimeException("Username already exists");
        }
        if (userService.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email already exists");
        }

        userService.createUser(request);
    }
}
