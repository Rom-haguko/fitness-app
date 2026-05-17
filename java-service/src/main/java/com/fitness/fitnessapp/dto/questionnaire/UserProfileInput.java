package com.fitness.fitnessapp.dto.questionnaire;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileInput {
    private long userId;
    private String goal;
    private String level;
    private int daysPerWeek;
    private Double weight;
    private Double height;
    private String splitType;
    private String restrictions;
    private Integer experienceMonths;
    private int age;
    private String gender;
}
