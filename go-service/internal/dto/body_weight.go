package dto

import "time"

type CreateBodyWeightRequest struct {
	UserID     int64     `json:"user_id"`
	Weight     float64   `json:"weight"`
	RecordedAt time.Time `json:"recorded_at"`
}
