package service

import (
	"context"
	"errors"
	"fmt"
	"strings"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/exporter"
)

var (
	ErrInvalidPlanID       = errors.New("invalid plan_id")
	ErrInvalidExportFormat = errors.New("invalid export format")
	ErrInvalidPlanName     = errors.New("invalid plan_name")
	ErrEmptyExportDays     = errors.New("export days are required")
	ErrInvalidExportDay    = errors.New("invalid export day")
	ErrEmptyExercises      = errors.New("export exercises are required")
	ErrInvalidExerciseName = errors.New("invalid exercise name")
	ErrInvalidExerciseSets = errors.New("invalid exercise sets")
	ErrInvalidExerciseReps = errors.New("invalid exercise reps")
)

type ExportResult struct {
	Content     []byte
	ContentType string
	FileName    string
}

type ExportService struct{}

func NewExportService() ExportService {
	return ExportService{}
}

func (s ExportService) ExportPlan(ctx context.Context, req dto.ExportPlanRequest) (ExportResult, error) {
	_ = ctx

	if req.UserID <= 0 {
		return ExportResult{}, ErrInvalidUserID
	}

	if req.PlanID <= 0 {
		return ExportResult{}, ErrInvalidPlanID
	}

	format := strings.ToLower(strings.TrimSpace(req.Format))
	if format != "txt" {
		return ExportResult{}, ErrInvalidExportFormat
	}

	if strings.TrimSpace(req.PlanName) == "" {
		return ExportResult{}, ErrInvalidPlanName
	}

	if len(req.Days) == 0 {
		return ExportResult{}, ErrEmptyExportDays
	}

	for _, day := range req.Days {
		if day.Day <= 0 {
			return ExportResult{}, ErrInvalidExportDay
		}

		if len(day.Exercises) == 0 {
			return ExportResult{}, ErrEmptyExercises
		}

		for _, exercise := range day.Exercises {
			if strings.TrimSpace(exercise.Name) == "" {
				return ExportResult{}, ErrInvalidExerciseName
			}

			if exercise.Sets <= 0 {
				return ExportResult{}, ErrInvalidExerciseSets
			}

			if strings.TrimSpace(exercise.Reps) == "" {
				return ExportResult{}, ErrInvalidExerciseReps
			}
		}
	}

	content, err := exporter.BuildTXT(req)
	if err != nil {
		return ExportResult{}, fmt.Errorf("build txt export: %w", err)
	}

	return ExportResult{
		Content:     content,
		ContentType: "text/plain; charset=utf-8",
		FileName:    fmt.Sprintf("plan_%d.txt", req.PlanID),
	}, nil
}