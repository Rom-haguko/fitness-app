package repository

import (
	"context"
	"fmt"

	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
	"github.com/jackc/pgx/v5/pgxpool"
)

type StatisticsRepository struct {
	db *pgxpool.Pool
}

func NewStatisticsRepository(db *pgxpool.Pool) StatisticsRepository {
	return StatisticsRepository{
		db: db,
	}
}

func (r StatisticsRepository) GetSummary(ctx context.Context, userID int64) (model.StatisticsSummary, error) {
	const query = `
		SELECT
			$1::bigint AS user_id,
			COUNT(pl.id)::int AS total_workouts,
			MAX(pl.performed_at) AS last_workout_date,
			(
				SELECT bw.weight
				FROM fitness_tracker.body_weight_logs bw
				WHERE bw.user_id = $1
				ORDER BY bw.recorded_at DESC
				LIMIT 1
			) AS current_body_weight,
			COALESCE(SUM(pl.sets * pl.reps * pl.weight), 0)::float8 AS total_volume
		FROM fitness_tracker.progress_logs pl
		WHERE pl.user_id = $1
	`

	var summary model.StatisticsSummary

	err := r.db.QueryRow(ctx, query, userID).Scan(
		&summary.UserID,
		&summary.TotalWorkouts,
		&summary.LastWorkoutDate,
		&summary.CurrentBodyWeight,
		&summary.TotalVolume,
	)
	if err != nil {
		return model.StatisticsSummary{}, fmt.Errorf("get statistics summary: %w", err)
	}

	return summary, nil
}

func (r StatisticsRepository) GetBodyWeightPoints(ctx context.Context, userID int64) ([]model.BodyWeightPoint, error) {
	const query = `
		SELECT
			TO_CHAR(recorded_at::date, 'YYYY-MM-DD') AS date,
			weight::float8 AS weight
		FROM fitness_tracker.body_weight_logs
		WHERE user_id = $1
		ORDER BY recorded_at ASC
	`

	rows, err := r.db.Query(ctx, query, userID)
	if err != nil {
		return nil, fmt.Errorf("query body weight points: %w", err)
	}
	defer rows.Close()

	var points []model.BodyWeightPoint

	for rows.Next() {
		var point model.BodyWeightPoint

		if err := rows.Scan(&point.Date, &point.Weight); err != nil {
			return nil, fmt.Errorf("scan body weight point: %w", err)
		}

		points = append(points, point)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate body weight points: %w", err)
	}

	return points, nil
}

func (r StatisticsRepository) GetVolumePoints(ctx context.Context, userID int64) ([]model.VolumePoint, error) {
	const query = `
		SELECT
			TO_CHAR(performed_at::date, 'YYYY-MM-DD') AS date,
			COALESCE(SUM(sets * reps * weight), 0)::float8 AS volume
		FROM fitness_tracker.progress_logs
		WHERE user_id = $1
		GROUP BY performed_at::date
		ORDER BY performed_at::date ASC
	`

	rows, err := r.db.Query(ctx, query, userID)
	if err != nil {
		return nil, fmt.Errorf("query volume points: %w", err)
	}
	defer rows.Close()

	var points []model.VolumePoint

	for rows.Next() {
		var point model.VolumePoint

		if err := rows.Scan(&point.Date, &point.Volume); err != nil {
			return nil, fmt.Errorf("scan volume point: %w", err)
		}

		points = append(points, point)
	}

	if err := rows.Err(); err != nil {
		return nil, fmt.Errorf("iterate volume points: %w", err)
	}

	return points, nil
}
