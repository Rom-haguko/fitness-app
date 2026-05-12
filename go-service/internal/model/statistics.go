package model

import "time"

type StatisticsSummary struct {
	UserID            int64
	TotalWorkouts     int
	LastWorkoutDate   *time.Time
	CurrentBodyWeight *float64
	TotalVolume       float64
}
