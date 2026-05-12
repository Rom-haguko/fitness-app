package service

import (
	"context"
	"errors"
	"testing"
	"time"

	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
)

type fakeStatisticsRepository struct {
	summary model.StatisticsSummary
	err     error
}

func (f fakeStatisticsRepository) GetSummary(ctx context.Context, userID int64) (model.StatisticsSummary, error) {
	return f.summary, f.err
}

func TestStatisticsService_GetSummary_Success(t *testing.T) {
	t.Parallel()

	lastWorkoutDate := time.Date(2026, 5, 12, 10, 0, 0, 0, time.UTC)
	currentBodyWeight := 79.5

	repo := fakeStatisticsRepository{
		summary: model.StatisticsSummary{
			UserID:            1,
			TotalWorkouts:     3,
			LastWorkoutDate:   &lastWorkoutDate,
			CurrentBodyWeight: &currentBodyWeight,
			TotalVolume:       7250.5,
		},
	}

	svc := NewStatisticsService(repo)

	resp, err := svc.GetSummary(context.Background(), 1)
	if err != nil {
		t.Fatalf("expected nil error, got %v", err)
	}

	if resp.UserID != 1 {
		t.Fatalf("expected user id 1, got %d", resp.UserID)
	}

	if resp.TotalWorkouts != 3 {
		t.Fatalf("expected total workouts 3, got %d", resp.TotalWorkouts)
	}

	if resp.LastWorkoutDate == nil || !resp.LastWorkoutDate.Equal(lastWorkoutDate) {
		t.Fatalf("expected last workout date %v, got %v", lastWorkoutDate, resp.LastWorkoutDate)
	}

	if resp.CurrentBodyWeight == nil || *resp.CurrentBodyWeight != currentBodyWeight {
		t.Fatalf("expected current body weight %v, got %v", currentBodyWeight, resp.CurrentBodyWeight)
	}

	if resp.TotalVolume != 7250.5 {
		t.Fatalf("expected total volume 7250.5, got %f", resp.TotalVolume)
	}
}

func TestStatisticsService_GetSummary_InvalidUserID(t *testing.T) {
	t.Parallel()

	repo := fakeStatisticsRepository{}
	svc := NewStatisticsService(repo)

	_, err := svc.GetSummary(context.Background(), 0)
	if !errors.Is(err, ErrInvalidUserID) {
		t.Fatalf("expected error %v, got %v", ErrInvalidUserID, err)
	}
}

func TestStatisticsService_GetSummary_RepositoryError(t *testing.T) {
	t.Parallel()

	expectedErr := errors.New("repository error")

	repo := fakeStatisticsRepository{
		err: expectedErr,
	}

	svc := NewStatisticsService(repo)

	_, err := svc.GetSummary(context.Background(), 1)
	if !errors.Is(err, expectedErr) {
		t.Fatalf("expected error %v, got %v", expectedErr, err)
	}
}