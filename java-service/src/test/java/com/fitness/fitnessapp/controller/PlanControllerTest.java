package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.service.UserService;
import com.fitness.fitnessapp.service.WorkoutPlanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutPlanService workoutPlanService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("GET /plans should return plan history view")
    void showPlanHistory_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(workoutPlanService.getUserPlans(1L)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/plans"))
                .andExpect(status().isOk())
                .andExpect(view().name("plan/history"))
                .andExpect(model().attributeExists("plans"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("GET /plans/{id} should return details view")
    void showPlanDetails_Success() throws Exception {
        User user = new User();
        user.setId(1L);
        WorkoutPlan plan = new WorkoutPlan();
        plan.setId(10L);
        plan.setGoal("Strength");

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(workoutPlanService.getUserPlan(1L, 10L)).thenReturn(plan);

        mockMvc.perform(get("/plans/10"))
                .andExpect(status().isOk())
                .andExpect(view().name("plan/details"))
                .andExpect(model().attributeExists("plan"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("GET /plans/{id} should 404 when plan not found")
    void showPlanDetails_NotFound() throws Exception {
        // Given
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(new User()));
        when(workoutPlanService.getUserPlan(anyLong(), eq(99L)))
                .thenThrow(new com.fitness.fitnessapp.exception.NotFoundException("Not found"));

        // When & Then
        mockMvc.perform(get("/plans/99"))
                .andExpect(status().isNotFound())
                .andExpect(view().name("error")); 
    }
}