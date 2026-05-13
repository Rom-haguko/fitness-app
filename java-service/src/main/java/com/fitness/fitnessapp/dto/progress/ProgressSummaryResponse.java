package com.fitness.fitnessapp.dto.progress;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgressSummaryResponse {
    @JsonProperty("total_workouts")
    private int totalWorkouts;

    @JsonProperty("last_workout_date")
    private String lastWorkoutDate;

    @JsonProperty("current_body_weight")
    private Double currentWeight;

    @JsonProperty("total_volume")
    private double totalVolume;
}
