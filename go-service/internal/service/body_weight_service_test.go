package service

import (
	"context"
	"errors"
	"testing"
	"time"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
)

type fakeBodyWeightRepository struct {
	createdLog model.BodyWeightLog
	err        error
}

func (f *fakeBodyWeightRepository) Create(ctx context.Context, log model.BodyWeightLog) error {
	f.createdLog = log
	return f.err
}

func TestBodyWeightService_CreateBodyWeight_Success(t *testing.T) {
	t.Parallel()

	recordedAt := time.Date(2026, 5, 12, 9, 0, 0, 0, time.UTC)

	repo := &fakeBodyWeightRepository{}
	svc := NewBodyWeightService(repo)

	err := svc.CreateBodyWeight(context.Background(), dto.CreateBodyWeightRequest{
		UserID:     1,
		Weight:     79.5,
		RecordedAt: recordedAt,
	})
	if err != nil {
		t.Fatalf("expected nil error, got %v", err)
	}

	if repo.createdLog.UserID != 1 {
		t.Fatalf("expected user id 1, got %d", repo.createdLog.UserID)
	}

	if repo.createdLog.Weight != 79.5 {
		t.Fatalf("expected weight 79.5, got %f", repo.createdLog.Weight)
	}

	if !repo.createdLog.RecordedAt.Equal(recordedAt) {
		t.Fatalf("expected recorded_at %v, got %v", recordedAt, repo.createdLog.RecordedAt)
	}
}

func TestBodyWeightService_CreateBodyWeight_DefaultRecordedAt(t *testing.T) {
	t.Parallel()

	repo := &fakeBodyWeightRepository{}
	svc := NewBodyWeightService(repo)

	err := svc.CreateBodyWeight(context.Background(), dto.CreateBodyWeightRequest{
		UserID: 1,
		Weight: 79.5,
	})
	if err != nil {
		t.Fatalf("expected nil error, got %v", err)
	}

	if repo.createdLog.RecordedAt.IsZero() {
		t.Fatal("expected recorded_at to be set")
	}
}

func TestBodyWeightService_CreateBodyWeight_ValidationErrors(t *testing.T) {
	t.Parallel()

	tests := []struct {
		name        string
		request     dto.CreateBodyWeightRequest
		expectedErr error
	}{
		{
			name: "invalid user id",
			request: dto.CreateBodyWeightRequest{
				UserID: 0,
				Weight: 79.5,
			},
			expectedErr: ErrInvalidUserID,
		},
		{
			name: "invalid body weight zero",
			request: dto.CreateBodyWeightRequest{
				UserID: 1,
				Weight: 0,
			},
			expectedErr: ErrInvalidBodyWeight,
		},
		{
			name: "invalid body weight negative",
			request: dto.CreateBodyWeightRequest{
				UserID: 1,
				Weight: -1,
			},
			expectedErr: ErrInvalidBodyWeight,
		},
	}

	for _, tt := range tests {
		tt := tt

		t.Run(tt.name, func(t *testing.T) {
			t.Parallel()

			repo := &fakeBodyWeightRepository{}
			svc := NewBodyWeightService(repo)

			err := svc.CreateBodyWeight(context.Background(), tt.request)
			if !errors.Is(err, tt.expectedErr) {
				t.Fatalf("expected error %v, got %v", tt.expectedErr, err)
			}
		})
	}
}

func TestBodyWeightService_CreateBodyWeight_RepositoryError(t *testing.T) {
	t.Parallel()

	expectedErr := errors.New("repository error")

	repo := &fakeBodyWeightRepository{
		err: expectedErr,
	}
	svc := NewBodyWeightService(repo)

	err := svc.CreateBodyWeight(context.Background(), dto.CreateBodyWeightRequest{
		UserID: 1,
		Weight: 79.5,
	})
	if !errors.Is(err, expectedErr) {
		t.Fatalf("expected error %v, got %v", expectedErr, err)
	}
}
