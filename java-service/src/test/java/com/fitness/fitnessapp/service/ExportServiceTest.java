package com.fitness.fitnessapp.service;

import com.fitness.fitnessapp.dto.export.ExportPlanRequest;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.entity.WorkoutPlanItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExportServiceTest {

    @Mock private GoProgressClientService goProgressClientService;
    @Mock private WorkoutPlanService workoutPlanService;

    @InjectMocks
    private ExportService exportService;

    @Test
    @DisplayName("Should successfully prepare export request and call Go service")
    void downloadPlan_ShouldGroupExercisesAndCallGo() {
        // Given
        WorkoutPlan plan = new WorkoutPlan();
        plan.setId(1L);
        plan.setGoal("Muscle Gain");

        WorkoutPlanItem item1 = new WorkoutPlanItem();
        item1.setExerciseName("Bench Press");
        item1.setSetsCount(3);
        item1.setRepsText("10");
        item1.setDayNumber(1);
        item1.setFocus("Chest");

        plan.setItems(List.of(item1));

        when(workoutPlanService.getUserPlan(1L, 1L)).thenReturn(plan);
        when(goProgressClientService.exportPlan(any(ExportPlanRequest.class)))
                .thenReturn(ResponseEntity.ok("pdf-content".getBytes()));

        // When
        ArgumentCaptor<ExportPlanRequest> captor = ArgumentCaptor.forClass(ExportPlanRequest.class);
        exportService.downloadPlan(1L, 1L, "pdf");

        // Then
        verify(goProgressClientService).exportPlan(captor.capture());
        ExportPlanRequest capturedRequest = captor.getValue();

        assertEquals(1L, capturedRequest.getPlanId());
        assertEquals("pdf", capturedRequest.getFormat());
        assertEquals(1, capturedRequest.getDays().size());
        assertEquals("Bench Press", capturedRequest.getDays().get(0).getExercises().get(0).getName());
    }
}