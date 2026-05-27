package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.plan.GeneratePlanResponse;
import com.fitness.fitnessapp.dto.plan.PlanDayDto;
import com.fitness.fitnessapp.dto.questionnaire.QuestionnaireForm;
import com.fitness.fitnessapp.entity.User;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.exception.ExternalServiceException;
import com.fitness.fitnessapp.exception.NotFoundException;
import com.fitness.fitnessapp.mapper.WorkoutPlanMapper;
import com.fitness.fitnessapp.repository.UserRepository;
import com.fitness.fitnessapp.repository.WorkoutPlanRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkoutPlanServiceTest {

    @Mock private WorkoutPlanRepository workoutPlanRepository;
    @Mock private UserRepository userRepository;
    @Mock private PythonPlanClientService pythonPlanClientService;
    @Mock private WorkoutPlanMapper workoutPlanMapper;

    @InjectMocks
    private WorkoutPlanService workoutPlanService;

    @Test
    @DisplayName("generatePlan should throw ExternalServiceException when Python service is offline")
    void generatePlan_ShouldThrowException_WhenPythonOffline() {
        // Given
        QuestionnaireForm form = new QuestionnaireForm();
        form.setGoal("muscle_gain");
        when(pythonPlanClientService.healthCheck()).thenReturn(false);

        // When & Then
        assertThrows(ExternalServiceException.class, () -> workoutPlanService.generatePlan(new User(), form));
        verify(pythonPlanClientService, never()).generatePlan(any());
    }

    @Test
    @DisplayName("saveGeneratedPlan should map and save correctly")
    void saveGeneratedPlan_ShouldSaveToDb() {
        // Given
        User user = new User();
        user.setId(1L);
        QuestionnaireForm form = new QuestionnaireForm();
        form.setGoal("strength");
        form.setDaysPerWeek(3);
        GeneratePlanResponse response = new GeneratePlanResponse();
        response.setWorkoutPlan(new ArrayList<PlanDayDto>());

        WorkoutPlan plan = new WorkoutPlan();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(workoutPlanMapper.mapResponseToEntity(response)).thenReturn(plan);
        when(workoutPlanRepository.save(any(WorkoutPlan.class))).thenReturn(plan);

        // When
        WorkoutPlan saved = workoutPlanService.saveGeneratedPlan(1L, form, response);

        // Then
        assertNotNull(saved);
        verify(workoutPlanRepository, times(1)).save(any(WorkoutPlan.class));
        assertEquals("strength", plan.getGoal());
    }

    @Test
    @DisplayName("getUserPlan should throw NotFoundException when plan is not found")
    void getUserPlan_ShouldThrowNotFound() {
        // Given
        when(workoutPlanRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(NotFoundException.class, () -> workoutPlanService.getUserPlan(1L, 1L));
    }
}