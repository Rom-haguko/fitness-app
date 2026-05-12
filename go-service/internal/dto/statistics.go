package dto

import "time"

type StatisticsSummaryResponse struct {
	UserID            int64      `json:"user_id"`
	TotalWorkouts     int        `json:"total_workouts"`
	LastWorkoutDate   *time.Time `json:"last_workout_date"`
	CurrentBodyWeight *float64   `json:"current_body_weight"`
	TotalVolume       float64    `json:"total_volume"`
}

type BodyWeightChartResponse struct {
	UserID int64             `json:"user_id"`
	Points []BodyWeightPoint `json:"points"`
}

type BodyWeightPoint struct {
	Date   string  `json:"date"`
	Weight float64 `json:"weight"`
}
