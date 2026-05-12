package service

import (
	"context"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
)

type StatisticsRepository interface {
	GetSummary(ctx context.Context, userID int64) (model.StatisticsSummary, error)
}

type StatisticsService struct {
	repository StatisticsRepository
}

func NewStatisticsService(repository StatisticsRepository) StatisticsService {
	return StatisticsService{
		repository: repository,
	}
}

func (s StatisticsService) GetSummary(ctx context.Context, userID int64) (dto.StatisticsSummaryResponse, error) {
	if userID <= 0 {
		return dto.StatisticsSummaryResponse{}, ErrInvalidUserID
	}

	summary, err := s.repository.GetSummary(ctx, userID)
	if err != nil {
		return dto.StatisticsSummaryResponse{}, err
	}

	return dto.StatisticsSummaryResponse{
		UserID:            summary.UserID,
		TotalWorkouts:     summary.TotalWorkouts,
		LastWorkoutDate:   summary.LastWorkoutDate,
		CurrentBodyWeight: summary.CurrentBodyWeight,
		TotalVolume:       summary.TotalVolume,
	}, nil
}
