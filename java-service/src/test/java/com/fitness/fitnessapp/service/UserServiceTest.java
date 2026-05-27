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

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    @DisplayName("createUser should encode password and save user")
    void createUser_ShouldSaveUser() {
        // Given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setEmail("test@test.com");
        request.setPassword("password123");

        when(passwordEncoder.encode("password123")).thenReturn("encoded_hash");

        User savedUser = new User();
        savedUser.setId(1L);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // When
        userService.createUser(request);

        // Then
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("getById should return user when user exists")
    void getById_ShouldReturnUser() {
        // Given
        User user = new User();
        user.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User result = userService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("getById should throw NotFoundException when user does not exist")
    void getById_ShouldThrowException() {
        // Given
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When/Then
        assertThrows(NotFoundException.class, () -> userService.getById(99L));
    }

    @Test
    @DisplayName("existsByUsername should return boolean from repository")
    void existsByUsername_ShouldReturnTrue() {
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        boolean result = userService.existsByUsername("existingUser");

        assertTrue(result);
        verify(userRepository).existsByUsername("existingUser");
    }
}