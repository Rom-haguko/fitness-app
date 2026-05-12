package service

import (
	"context"
	"errors"
	"time"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/model"
)

var (
	ErrInvalidBodyWeight = errors.New("invalid body weight")
)

type BodyWeightRepository interface {
	Create(ctx context.Context, log model.BodyWeightLog) error
}

type BodyWeightService struct {
	repository BodyWeightRepository
}

func NewBodyWeightService(repository BodyWeightRepository) BodyWeightService {
	return BodyWeightService{
		repository: repository,
	}
}

func (s BodyWeightService) CreateBodyWeight(ctx context.Context, req dto.CreateBodyWeightRequest) error {
	if req.UserID <= 0 {
		return ErrInvalidUserID
	}

	if req.Weight <= 0 {
		return ErrInvalidBodyWeight
	}

	recordedAt := req.RecordedAt
	if recordedAt.IsZero() {
		recordedAt = time.Now().UTC()
	}

	log := model.BodyWeightLog{
		UserID:     req.UserID,
		Weight:     req.Weight,
		RecordedAt: recordedAt,
	}

	if err := s.repository.Create(ctx, log); err != nil {
		return err
	}

	return nil
}
