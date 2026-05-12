package model

import "time"

type BodyWeightLog struct {
	ID         int64
	UserID     int64
	Weight     float64
	RecordedAt time.Time
	CreatedAt  time.Time
}
