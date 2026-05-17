package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.auth.RegisterRequest;
import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.logging.LoggingUtils;
import com.fitness.fitnessapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(RegisterRequest request) {
        log.info("Trying to save user to database",kv("username",request.getUsername()));
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("User");

        User savedUser = userRepository.save(user);

        LoggingUtils.logEntityCreated(log, "User", savedUser.getId());
        LoggingUtils.logUserAction(log, "USER_REGISTERED", savedUser.getId(), savedUser.getUsername());
    }

    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }
    public User getById(Long id){
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User lookup failed", kv("user_id", id));
                    return new NotFoundException("User not found with id: " + id);
                });
    }
    public boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }
    public boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
