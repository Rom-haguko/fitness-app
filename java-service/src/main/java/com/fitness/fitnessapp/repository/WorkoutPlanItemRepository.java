package com.fitness.fitnessapp.repository;

import com.fitness.fitnessapp.entity.WorkoutPlanItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkoutPlanItemRepository extends JpaRepository<WorkoutPlanItem,Long> {
    List<WorkoutPlanItem> findAllByWorkoutPlanIdOrderByDayNumberAscExerciseOrderAsc(Long workoutPlanId);
}
