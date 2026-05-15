package com.fitness.fitnessapp.dto.export;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportPlanDay {
    private int day;
    private String focus;
    private List<ExportExercise> exercises;
}
