package com.fitness.fitnessapp.dto.plan;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneratePlanRequest {
    private String goal;

    private String level;

    @JsonProperty("days_per_week")
    private int daysPerWeek;

    private Double weight;

    private Double height;

    @JsonProperty("split_type")
    private String splitType;

    private String restrictions;
}
