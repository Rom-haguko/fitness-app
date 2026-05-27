package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.auth.RegisterRequest;
import com.fitness.fitnessapp.exception.ValidationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    @DisplayName("Should throw ValidationException when username is taken")
    void register_ShouldThrowException_WhenUsernameExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existingUser");
        request.setEmail("new@mail.com");

        when(userService.existsByUsername("existingUser")).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> authService.registerUser(request));
        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should throw ValidationException when email is taken")
    void register_ShouldThrowException_WhenEmailExists() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setEmail("taken@mail.com");

        when(userService.existsByUsername("newUser")).thenReturn(false);
        when(userService.existsByEmail("taken@mail.com")).thenReturn(true);

        // When & Then
        assertThrows(ValidationException.class, () -> authService.registerUser(request));
        verify(userService, never()).createUser(any());
    }

    @Test
    @DisplayName("Should call createUser when data is unique")
    void register_ShouldSaveUser_WhenDataIsUnique() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newUser");
        request.setEmail("new@mail.com");
        request.setPassword("password");
        request.setConfirmPassword("password"); // Убедись, что они идентичны

        when(userService.existsByUsername("newUser")).thenReturn(false);
        when(userService.existsByEmail("new@mail.com")).thenReturn(false);

        // When
        authService.registerUser(request); // Убедись, что вызываешь верный метод

        // Then
        verify(userService, times(1)).createUser(request);
    }
}