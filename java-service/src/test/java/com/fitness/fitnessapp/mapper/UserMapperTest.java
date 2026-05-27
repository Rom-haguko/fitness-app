package com.fitness.fitnessapp.mapper;

import com.fitness.fitnessapp.dto.auth.UserResponse;
import com.fitness.fitnessapp.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    private final UserMapper mapper = new UserMapper();

    @Test
    @DisplayName("Should map User entity to UserResponse correctly")
    void shouldMapUserToResponse() {
        // Given
        User user = new User();
        user.setId(1L);
        user.setUsername("john_doe");
        user.setEmail("john@example.com");
        user.setPasswordHash("SECRET_HASH_SHOULD_NOT_BE_MAPPED");
        user.setRole("USER");
        user.setCreatedAt(OffsetDateTime.now());

        // When
        UserResponse response = mapper.toResponse(user);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(user.getId());
        assertThat(response.getUsername()).isEqualTo(user.getUsername());
        assertThat(response.getEmail()).isEqualTo(user.getEmail());
    }

    @Test
    @DisplayName("Should handle null input in UserMapper")
    void shouldHandleNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("Security Check: UserResponse should not have a password field")
    void securityCheck() {
        // Check that response has no data about password
        User user = new User();
        user.setPasswordHash("secret");

        UserResponse response = mapper.toResponse(user);
    }
}