package repository

import (
	"context"
	"fmt"

	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
	"github.com/jackc/pgx/v5/pgxpool"
)

type BodyWeightRepository struct {
	db *pgxpool.Pool
}

func NewBodyWeightRepository(db *pgxpool.Pool) BodyWeightRepository {
	return BodyWeightRepository{
		db: db,
	}
}

func (r BodyWeightRepository) Create(ctx context.Context, log model.BodyWeightLog) error {
	const query = `
		INSERT INTO fitness_tracker.body_weight_logs (
			user_id,
			weight,
			recorded_at
		)
		VALUES ($1, $2, $3)
	`

	_, err := r.db.Exec(
		ctx,
		query,
		log.UserID,
		log.Weight,
		log.RecordedAt,
	)
	if err != nil {
		return fmt.Errorf("insert body weight log: %w", err)
	}

	return nil
}
