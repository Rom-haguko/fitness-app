package com.fitness.fitnessapp.mapper;

import com.fitness.fitnessapp.dto.plan.ExerciseDto;
import com.fitness.fitnessapp.dto.plan.GeneratePlanResponse;
import com.fitness.fitnessapp.dto.plan.PlanDayDto;
import com.fitness.fitnessapp.entity.WorkoutPlan;
import com.fitness.fitnessapp.entity.WorkoutPlanItem;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class WorkoutPlanMapper {
    public WorkoutPlan mapResponseToEntity(GeneratePlanResponse response){
        if (response == null || response.getWorkoutPlan() == null){
            return null;
        }

        WorkoutPlan plan = new WorkoutPlan();
        plan.setItems(new ArrayList<>());
        for (PlanDayDto dayDto : response.getWorkoutPlan()){
            int exerciseOrder = 1;
            for (ExerciseDto exerciseDto : dayDto.getExercises()){
                WorkoutPlanItem item = new WorkoutPlanItem();
                item.setDayNumber(dayDto.getDay());
                item.setFocus(dayDto.getFocus());
                item.setExerciseName(exerciseDto.getName());
                item.setSetsCount(exerciseDto.getSets());
                item.setRepsText(exerciseDto.getReps());
                item.setExerciseOrder(exerciseOrder++);

                plan.addItem(item);
            }
        }
        return plan;
    }
}
