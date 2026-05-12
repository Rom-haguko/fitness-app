package com.fitness.fitnessapp.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDayDto {
    private int day;
    private String focus;
    private List<ExerciseDto> exercises;
}
