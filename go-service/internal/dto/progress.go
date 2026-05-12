package dto

import "time"

type CreateProgressLogRequest struct {
	UserID            int64     `json:"user_id"`
	WorkoutPlanID     int64     `json:"workout_plan_id"`
	WorkoutPlanItemID *int64    `json:"workout_plan_item_id,omitempty"`
	ExerciseID        *int64    `json:"exercise_id,omitempty"`
	Sets              int       `json:"sets"`
	Reps              int       `json:"reps"`
	Weight            float64   `json:"weight"`
	PerformedAt       time.Time `json:"performed_at"`
}
