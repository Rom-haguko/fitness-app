package service

import (
	"context"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
)

type StatisticsRepository interface {
	GetSummary(ctx context.Context, userID int64) (model.StatisticsSummary, error)
	GetBodyWeightPoints(ctx context.Context, userID int64) ([]model.BodyWeightPoint, error)
	GetVolumePoints(ctx context.Context, userID int64) ([]model.VolumePoint, error)
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

func (s StatisticsService) GetBodyWeightChart(ctx context.Context, userID int64) (dto.BodyWeightChartResponse, error) {
	if userID <= 0 {
		return dto.BodyWeightChartResponse{}, ErrInvalidUserID
	}

	points, err := s.repository.GetBodyWeightPoints(ctx, userID)
	if err != nil {
		return dto.BodyWeightChartResponse{}, err
	}

	responsePoints := make([]dto.BodyWeightPoint, 0, len(points))
	for _, point := range points {
		responsePoints = append(responsePoints, dto.BodyWeightPoint{
			Date:   point.Date,
			Weight: point.Weight,
		})
	}

	return dto.BodyWeightChartResponse{
		UserID: userID,
		Points: responsePoints,
	}, nil
}

func (s StatisticsService) GetVolumeChart(ctx context.Context, userID int64) (dto.VolumeChartResponse, error) {
	if userID <= 0 {
		return dto.VolumeChartResponse{}, ErrInvalidUserID
	}

	points, err := s.repository.GetVolumePoints(ctx, userID)
	if err != nil {
		return dto.VolumeChartResponse{}, err
	}

	responsePoints := make([]dto.VolumePoint, 0, len(points))
	for _, point := range points {
		responsePoints = append(responsePoints, dto.VolumePoint{
			Date:   point.Date,
			Volume: point.Volume,
		})
	}

	return dto.VolumeChartResponse{
		UserID: userID,
		Points: responsePoints,
	}, nil
}
