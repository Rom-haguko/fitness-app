package com.fitness.fitnessapp.repository;

import com.fitness.fitnessapp.entity.WorkoutPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WorkoutPlanRepository extends JpaRepository<WorkoutPlan, Long> {
    List<WorkoutPlan> findAllByUserIdOrderByCreatedAtDesc(Long userId);
    Optional<WorkoutPlan> findByIdAndUserId(Long planId, Long userId);
}
