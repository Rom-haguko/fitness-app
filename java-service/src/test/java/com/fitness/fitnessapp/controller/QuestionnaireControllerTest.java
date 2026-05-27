package com.fitness.fitnessapp.controller;

import com.fitness.fitnessapp.dto.questionnaire.QuestionnaireForm;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class QuestionnaireControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private WorkoutPlanService workoutPlanService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("GET /questionnaire should return form page")
    void showQuestionnaireForm_Success() throws Exception {
        mockMvc.perform(get("/questionnaire"))
                .andExpect(status().isOk())
                .andExpect(view().name("questionnaire/form"))
                .andExpect(model().attributeExists("questionnaireForm"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("POST /questionnaire should process form and redirect to plan")
    void submitQuestionnaire_ValidData() throws Exception {
        // Given
        User user = new User();
        user.setId(1L);
        WorkoutPlan plan = new WorkoutPlan();
        plan.setId(5L);

        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(workoutPlanService.saveGeneratedPlan(any(), any(), any())).thenReturn(plan);

        // When & Then
        mockMvc.perform(post("/questionnaire")
                        .with(csrf())
                        .param("goal", "muscle_gain")
                        .param("level", "beginner")
                        .param("daysPerWeek", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/plans/5"));
    }

    @Test
    @WithMockUser(username = "testuser")
    @DisplayName("POST /questionnaire should return form with errors when data invalid")
    void submitQuestionnaire_InvalidData() throws Exception {
        mockMvc.perform(post("/questionnaire")
                        .with(csrf())
                        .param("goal", "") // Error: empty field
                        .param("daysPerWeek", "0")) // Error: days<1
                .andExpect(status().isOk())
                .andExpect(view().name("questionnaire/form"));
    }
}