package com.fitness.fitnessapp.dto.plan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDto {
    private String name;
    private int sets;
    private String reps;
}
