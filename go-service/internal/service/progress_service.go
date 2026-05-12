package service

import (
	"context"
	"errors"
	"time"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
)

var (
	ErrInvalidUserID            = errors.New("invalid user_id")
	ErrInvalidWorkoutPlanID     = errors.New("invalid workout_plan_id")
	ErrInvalidWorkoutPlanItemID = errors.New("invalid workout_plan_item_id")
	ErrInvalidExerciseID        = errors.New("invalid exercise_id")
	ErrMissingExerciseReference = errors.New("workout_plan_item_id or exercise_id is required")
	ErrInvalidSets              = errors.New("invalid sets")
	ErrInvalidReps              = errors.New("invalid reps")
	ErrInvalidWeight            = errors.New("invalid weight")
)

type ProgressRepository interface {
	Create(ctx context.Context, log model.ProgressLog) error
}

type ProgressService struct {
	repository ProgressRepository
}

func NewProgressService(repository ProgressRepository) ProgressService {
	return ProgressService{
		repository: repository,
	}
}

func (s ProgressService) CreateProgressLog(ctx context.Context, req dto.CreateProgressLogRequest) error {
	if req.UserID <= 0 {
		return ErrInvalidUserID
	}

	if req.WorkoutPlanID <= 0 {
		return ErrInvalidWorkoutPlanID
	}

	if req.WorkoutPlanItemID != nil && *req.WorkoutPlanItemID <= 0 {
		return ErrInvalidWorkoutPlanItemID
	}

	if req.ExerciseID != nil && *req.ExerciseID <= 0 {
		return ErrInvalidExerciseID
	}

	if req.WorkoutPlanItemID == nil && req.ExerciseID == nil {
		return ErrMissingExerciseReference
	}

	if req.Sets <= 0 {
		return ErrInvalidSets
	}

	if req.Reps <= 0 {
		return ErrInvalidReps
	}

	if req.Weight < 0 {
		return ErrInvalidWeight
	}

	performedAt := req.PerformedAt
	if performedAt.IsZero() {
		performedAt = time.Now().UTC()
	}

	log := model.ProgressLog{
		UserID:            req.UserID,
		WorkoutPlanID:     req.WorkoutPlanID,
		WorkoutPlanItemID: req.WorkoutPlanItemID,
		ExerciseID:        req.ExerciseID,
		Sets:              req.Sets,
		Reps:              req.Reps,
		Weight:            req.Weight,
		PerformedAt:       performedAt,
	}

	if err := s.repository.Create(ctx, log); err != nil {
		return err
	}

	return nil
}
