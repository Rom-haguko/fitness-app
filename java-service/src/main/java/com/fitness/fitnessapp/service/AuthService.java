package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.auth.RegisterRequest;
import com.fitness.fitnessapp.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private final UserService userService;

    @Transactional
    public void registerUser(RegisterRequest request){
        validateRegistration(request);
        userService.createUser(request);
    }

    public void validateRegistration(RegisterRequest request){
        if (userService.existsByUsername(request.getUsername())){
            log.warn("Registration failed: username already taken",
                    kv("username", request.getUsername()));
            throw new ValidationException("Username already exists");
        }
        if (userService.existsByEmail(request.getEmail())){
            log.warn("Registration failed: email already taken",
                    kv("email", request.getEmail()));
            throw new ValidationException("Email already exists");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new ValidationException("Passwords do not match");
        }
    }
}
