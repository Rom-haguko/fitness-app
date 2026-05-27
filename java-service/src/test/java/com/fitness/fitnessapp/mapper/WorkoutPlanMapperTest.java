package com.fitness.fitnessapp.mapper;

import com.fitness.fitnessapp.dto.plan.*;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class WorkoutPlanMapperTest {

    private final WorkoutPlanMapper mapper = new WorkoutPlanMapper();

    @Test
    @DisplayName("Should correctly map Python response to DB entities")
    void shouldMapResponseToEntity() {
        ExerciseDto ex = new ExerciseDto("Pushups", 3, "15");
        PlanDayDto day = new PlanDayDto(1, "Chest", List.of(ex));
        GeneratePlanResponse response = new GeneratePlanResponse();
        response.setWorkoutPlan(List.of(day));

        WorkoutPlan plan = mapper.mapResponseToEntity(response);

        assertThat(plan.getItems()).hasSize(1);
        assertThat(plan.getItems().get(0).getExerciseName()).isEqualTo("Pushups");
        assertThat(plan.getItems().get(0).getDayNumber()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should return null when response is empty")
    void shouldReturnNullOnEmptyResponse() {
        assertThat(mapper.mapResponseToEntity(null)).isNull();
    }
}