package service

import (
	"context"
	"errors"
	"testing"
	"time"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
)

func TestProgressService_CreateProgressLog_Success(t *testing.T) {
	t.Parallel()

	exerciseID := int64(3)
	performedAt := time.Date(2026, 5, 12, 10, 0, 0, 0, time.UTC)

	svc := NewProgressService()

	log, err := svc.CreateProgressLog(context.Background(), dto.CreateProgressLogRequest{
		UserID:        1,
		WorkoutPlanID: 2,
		ExerciseID:    &exerciseID,
		Sets:          4,
		Reps:          10,
		Weight:        60.5,
		PerformedAt:   performedAt,
	})
	if err != nil {
		t.Fatalf("expected nil error, got %v", err)
	}

	if log.UserID != 1 {
		t.Fatalf("expected user id 1, got %d", log.UserID)
	}

	if log.WorkoutPlanID != 2 {
		t.Fatalf("expected workout plan id 2, got %d", log.WorkoutPlanID)
	}

	if log.ExerciseID == nil || *log.ExerciseID != exerciseID {
		t.Fatalf("expected exercise id %d, got %v", exerciseID, log.ExerciseID)
	}

	if log.Sets != 4 {
		t.Fatalf("expected sets 4, got %d", log.Sets)
	}

	if log.Reps != 10 {
		t.Fatalf("expected reps 10, got %d", log.Reps)
	}

	if log.Weight != 60.5 {
		t.Fatalf("expected weight 60.5, got %f", log.Weight)
	}

	if !log.PerformedAt.Equal(performedAt) {
		t.Fatalf("expected performed_at %v, got %v", performedAt, log.PerformedAt)
	}
}

func TestProgressService_CreateProgressLog_DefaultPerformedAt(t *testing.T) {
	t.Parallel()

	exerciseID := int64(3)

	svc := NewProgressService()

	log, err := svc.CreateProgressLog(context.Background(), dto.CreateProgressLogRequest{
		UserID:        1,
		WorkoutPlanID: 2,
		ExerciseID:    &exerciseID,
		Sets:          4,
		Reps:          10,
		Weight:        60.5,
	})
	if err != nil {
		t.Fatalf("expected nil error, got %v", err)
	}

	if log.PerformedAt.IsZero() {
		t.Fatal("expected performed_at to be set")
	}
}

func TestProgressService_CreateProgressLog_ValidationErrors(t *testing.T) {
	t.Parallel()

	validExerciseID := int64(3)
	invalidExerciseID := int64(0)

	tests := []struct {
		name        string
		request     dto.CreateProgressLogRequest
		expectedErr error
	}{
		{
			name: "invalid user id",
			request: dto.CreateProgressLogRequest{
				UserID:        0,
				WorkoutPlanID: 2,
				ExerciseID:    &validExerciseID,
				Sets:          4,
				Reps:          10,
				Weight:        60.5,
			},
			expectedErr: ErrInvalidUserID,
		},
		{
			name: "invalid workout plan id",
			request: dto.CreateProgressLogRequest{
				UserID:        1,
				WorkoutPlanID: 0,
				ExerciseID:    &validExerciseID,
				Sets:          4,
				Reps:          10,
				Weight:        60.5,
			},
			expectedErr: ErrInvalidWorkoutPlanID,
		},
		{
			name: "missing exercise reference",
			request: dto.CreateProgressLogRequest{
				UserID:        1,
				WorkoutPlanID: 2,
				Sets:          4,
				Reps:          10,
				Weight:        60.5,
			},
			expectedErr: ErrMissingExerciseReference,
		},
		{
			name: "invalid exercise id",
			request: dto.CreateProgressLogRequest{
				UserID:        1,
				WorkoutPlanID: 2,
				ExerciseID:    &invalidExerciseID,
				Sets:          4,
				Reps:          10,
				Weight:        60.5,
			},
			expectedErr: ErrInvalidExerciseID,
		},
		{
			name: "invalid sets",
			request: dto.CreateProgressLogRequest{
				UserID:        1,
				WorkoutPlanID: 2,
				ExerciseID:    &validExerciseID,
				Sets:          0,
				Reps:          10,
				Weight:        60.5,
			},
			expectedErr: ErrInvalidSets,
		},
		{
			name: "invalid reps",
			request: dto.CreateProgressLogRequest{
				UserID:        1,
				WorkoutPlanID: 2,
				ExerciseID:    &validExerciseID,
				Sets:          4,
				Reps:          0,
				Weight:        60.5,
			},
			expectedErr: ErrInvalidReps,
		},
		{
			name: "invalid weight",
			request: dto.CreateProgressLogRequest{
				UserID:        1,
				WorkoutPlanID: 2,
				ExerciseID:    &validExerciseID,
				Sets:          4,
				Reps:          10,
				Weight:        -1,
			},
			expectedErr: ErrInvalidWeight,
		},
	}

	svc := NewProgressService()

	for _, tt := range tests {
		tt := tt

		t.Run(tt.name, func(t *testing.T) {
			t.Parallel()

			_, err := svc.CreateProgressLog(context.Background(), tt.request)
			if !errors.Is(err, tt.expectedErr) {
				t.Fatalf("expected error %v, got %v", tt.expectedErr, err)
			}
		})
	}
}