package com.fitness.fitnessapp.dto.export;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExportPlanRequest {
    @JsonProperty("user_id")
    private long userId;
    @JsonProperty("plan_id")
    private long planId;
    private String format;
    @JsonProperty("plan_name")
    private String planName;
    private List<ExportPlanDay> days;
}
