package com.fitness.fitnessapp.dto.progress;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutLogForm {
    @NotNull(message = "Plan id is missing")
    private Long workoutPlanId;

    @NotNull(message = "Exercise id is missing")
    private Long exerciseId;

    @Min(value = 1, message = "Sets must be at least 1")
    private int sets;

    @Min(value = 1, message = "Reps must be at least 1")
    private int reps;

    @Min(value = 0, message = "Weight must be positive")
    private double weight;
}
