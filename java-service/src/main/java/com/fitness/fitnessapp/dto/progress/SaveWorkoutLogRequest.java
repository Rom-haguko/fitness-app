package com.fitness.fitnessapp.dto.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveWorkoutLogRequest {
    @JsonProperty("user_id")
    private long userId;

    @JsonProperty("workout_plan_id")
    private long workoutPlanId;

    @JsonProperty("exercise_id")
    private long exerciseId;

    private int sets;
    private int reps;
    private double weight;
}
