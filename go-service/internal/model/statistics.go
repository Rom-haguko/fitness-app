package model

import "time"

type StatisticsSummary struct {
	UserID            int64
	TotalWorkouts     int
	LastWorkoutDate   *time.Time
	CurrentBodyWeight *float64
	TotalVolume       float64
}

type BodyWeightPoint struct {
	Date   string
	Weight float64
}

type VolumePoint struct {
	Date   string
	Volume float64
}
