package repository

import (
	"context"
	"fmt"

	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
	"github.com/jackc/pgx/v5/pgxpool"
)

type ProgressRepository struct {
	db *pgxpool.Pool
}

func NewProgressRepository(db *pgxpool.Pool) ProgressRepository {
	return ProgressRepository{
		db: db,
	}
}

func (r ProgressRepository) Create(ctx context.Context, log model.ProgressLog) error {
	const query = `
		INSERT INTO fitness_tracker.progress_logs (
			user_id,
			workout_plan_id,
			workout_plan_item_id,
			exercise_id,
			sets,
			reps,
			weight,
			performed_at
		)
		VALUES ($1, $2, $3, $4, $5, $6, $7, $8)
	`

	_, err := r.db.Exec(
		ctx,
		query,
		log.UserID,
		log.WorkoutPlanID,
		log.WorkoutPlanItemID,
		log.ExerciseID,
		log.Sets,
		log.Reps,
		log.Weight,
		log.PerformedAt,
	)
	if err != nil {
		return fmt.Errorf("insert progress log: %w", err)
	}

	return nil
}
