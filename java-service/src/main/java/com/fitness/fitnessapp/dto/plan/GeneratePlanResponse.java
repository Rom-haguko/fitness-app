package com.fitness.fitnessapp.dto.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePlanResponse {
    private String goal;

    @JsonProperty("days_per_week")
    private int daysPerWeek;

    @JsonProperty("workout_plan")
    private List<PlanDayDto> workoutPlan;
}
