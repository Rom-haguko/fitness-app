package com.fitness.fitnessapp.mapper;

import com.fitness.fitnessapp.dto.auth.UserResponse;
import com.fitness.fitnessapp.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    public UserResponse toResponse(User user){
        if (user == null) {
            return null;
        }

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setEmail(user.getEmail());

        return response;
    }
}
