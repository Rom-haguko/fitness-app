package com.fitness.fitnessapp.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "workout_plan_items", schema = "fitness_tracker")
@Data
public class WorkoutPlanItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workout_plan_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private WorkoutPlan workoutPlan;

    @Column(nullable = false)
    private int dayNumber;

    @Column
    private String focus;

    @Column(nullable = false)
    private String exerciseName;

    @Column
    private int setsCount;

    @Column
    private String repsText;

    @Column
    private int exerciseOrder;
}
