package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.auth.RegisterRequest;
import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @InjectMocks private UserService userService;

    @Test
    @DisplayName("Create user should encode password and call repository")
    void createUser_Success() {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("ivan");
        request.setPassword("123");
        request.setEmail("ivan@test.com");

        when(passwordEncoder.encode("123")).thenReturn("encoded_hash");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        userService.createUser(request);

        verify(userRepository, times(1)).save(any(User.class));
        verify(passwordEncoder).encode("123");
    }

    @Test
    @DisplayName("GetById should throw NotFoundException when user missing")
    void getById_NotFound() {
        when(userRepository.findById(100L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.getById(100L));
    }
}