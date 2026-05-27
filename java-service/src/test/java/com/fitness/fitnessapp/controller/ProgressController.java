package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.dto.progress.ProgressSummaryResponse;
import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.service.GoProgressClientService;
import com.fitness.fitnessapp.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GoProgressClientService goProgressClientService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("GET /progress should return summary page with stats")
    void showProgressSummary_Success() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        ProgressSummaryResponse summary = new ProgressSummaryResponse();
        summary.setTotalWorkouts(10);
        when(goProgressClientService.getProgressSummary(1L)).thenReturn(summary);

        // When & Then
        mockMvc.perform(get("/progress"))
                .andExpect(status().isOk())
                .andExpect(view().name("progress/summary"))
                .andExpect(model().attributeExists("summary"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("POST /progress/log should redirect after saving log")
    void submitWorkoutLog_Success() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        // When & Then
        mockMvc.perform(post("/progress/log")
                        .with(csrf())
                        .param("workoutPlanId", "1")
                        .param("workoutPlanItemId", "1")
                        .param("sets", "3")
                        .param("reps", "10")
                        .param("weight", "50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/progress?success=true"));
    }
}