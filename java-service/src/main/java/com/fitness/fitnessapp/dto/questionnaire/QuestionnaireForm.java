package com.fitness.fitnessapp.dto.questionnaire;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionnaireForm {
    @NotBlank(message = "Please select your goal")
    private String goal;

    @NotBlank(message = "Please select your current fitness level")
    private String level;

    @NotNull(message = "Please specify how many days per week you can train")
    @Min(value = 1, message = "Minimum 1 day per week")
    @Max(value = 7, message = "Maximum 7 days per week")
    private Integer daysPerWeek;

    @Min(value = 0, message = "Weight cannot be negative")
    private Double weight;

    @Min(value = 0, message = "Height cannot be negative")
    private Double height;

    private String splitType;

    private String restrictions;

    @Min(value = 0, message = "Experience cannot be negative")
    private Integer experienceMonths;
}
