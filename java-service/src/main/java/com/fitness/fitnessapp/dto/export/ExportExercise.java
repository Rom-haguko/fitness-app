package com.fitness.fitnessapp.dto.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportExercise {
    private String name;
    private int sets;
    private String reps;
}
