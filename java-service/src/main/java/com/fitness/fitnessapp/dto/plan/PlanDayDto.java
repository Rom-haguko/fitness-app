package com.fitness.fitnessapp.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDayDto {
    private int day;
    private String focus;
    private List<ExerciseDto> exercises;
}
