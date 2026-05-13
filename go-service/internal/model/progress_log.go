package model

import "time"

type ProgressLog struct {
	ID                int64
	UserID            int64
	WorkoutPlanID     int64
	WorkoutPlanItemID *int64
	ExerciseID        *int64
	Sets              int
	Reps              int
	Weight            float64
	PerformedAt       time.Time
	CreatedAt         time.Time
}
