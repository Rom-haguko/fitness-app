package com.fitness.fitnessapp.mapper;

import com.fitness.fitnessapp.dto.plan.*;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.entity.WorkoutPlanItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class WorkoutPlanMapperTest {

    private final WorkoutPlanMapper mapper = new WorkoutPlanMapper();

    @Test
    @DisplayName("Should correctly map Python response to DB entities")
    void shouldMapResponseToEntity() {
        // Given
        ExerciseDto ex = new ExerciseDto("Pushups", 3, "15");
        PlanDayDto day = new PlanDayDto(1, "Chest", List.of(ex));
        GeneratePlanResponse response = new GeneratePlanResponse();
        response.setWorkoutPlan(List.of(day));
        // When
        WorkoutPlan plan = mapper.mapResponseToEntity(response);
        // Then
        assertThat(plan.getItems()).hasSize(1);
        assertThat(plan.getItems().get(0).getExerciseName()).isEqualTo("Pushups");
        assertThat(plan.getItems().get(0).getDayNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return null when response is empty")
    void shouldReturnNullOnEmptyResponse() {
        assertThat(mapper.mapResponseToEntity(null)).isNull();
    }

    @Test
    @DisplayName("Should correctly set exercise order and reset it for each day")
    void shouldSetCorrectOrder() {
        // Given: 2 exercises for 2 days
        ExerciseDto ex1 = new ExerciseDto("Ex 1", 3, "10");
        ExerciseDto ex2 = new ExerciseDto("Ex 2", 3, "10");

        PlanDayDto day1 = new PlanDayDto(1, "Focus 1", List.of(ex1, ex2));
        PlanDayDto day2 = new PlanDayDto(2, "Focus 2", List.of(ex1, ex2));

        GeneratePlanResponse response = new GeneratePlanResponse();
        response.setWorkoutPlan(List.of(day1, day2));

        // When
        WorkoutPlan plan = mapper.mapResponseToEntity(response);

        // Then
        List<WorkoutPlanItem> items = plan.getItems();
        assertThat(items).hasSize(4);

        // Checks order numbers
        assertThat(items.get(0).getExerciseOrder()).isEqualTo(1);
        assertThat(items.get(1).getExerciseOrder()).isEqualTo(2);
        assertThat(items.get(2).getExerciseOrder()).isEqualTo(1);
        assertThat(items.get(3).getExerciseOrder()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should establish bidirectional link between Plan and Items")
    void shouldEstablishLinks() {
        // Given
        ExerciseDto ex = new ExerciseDto("Pushups", 3, "15");
        PlanDayDto day = new PlanDayDto(1, "Chest", List.of(ex));
        GeneratePlanResponse response = new GeneratePlanResponse();
        response.setWorkoutPlan(List.of(day));

        // When
        WorkoutPlan plan = mapper.mapResponseToEntity(response);

        // Then
        WorkoutPlanItem item = plan.getItems().get(0);
        // Link in exercise should point to parent's plan
        assertThat(item.getWorkoutPlan()).isNotNull();
        assertThat(item.getWorkoutPlan()).isEqualTo(plan);
    }

    @Test
    @DisplayName("Should handle days with empty exercise lists")
    void shouldHandleEmptyDays() {
        // Given: day without exercises
        PlanDayDto day = new PlanDayDto(1, "Rest Day", java.util.Collections.emptyList());
        GeneratePlanResponse response = new GeneratePlanResponse();
        response.setWorkoutPlan(List.of(day));

        // When
        WorkoutPlan plan = mapper.mapResponseToEntity(response);

        // Then
        assertThat(plan.getItems()).isEmpty();
    }


}