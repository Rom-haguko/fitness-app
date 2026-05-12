package handler

import (
	"context"
	"encoding/json"
	"errors"
	"fmt"
	"log/slog"
	"net/http"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/response"
	"github.com/Rom-haguko/fitness-app/go-service/internal/service"
)

type ExportService interface {
	ExportPlan(ctx context.Context, req dto.ExportPlanRequest) (service.ExportResult, error)
}

type ExportHandler struct {
	service ExportService
	log     *slog.Logger
}

func NewExportHandler(service ExportService, log *slog.Logger) ExportHandler {
	return ExportHandler{
		service: service,
		log:     log,
	}
}

func (h ExportHandler) ExportPlan(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		if err := response.WriteError(w, http.StatusMethodNotAllowed, "method not allowed"); err != nil {
			h.log.Error("failed to write method not allowed response", slog.Any("error", err))
		}
		return
	}

	var req dto.ExportPlanRequest

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		if writeErr := response.WriteError(w, http.StatusBadRequest, "invalid request body"); writeErr != nil {
			h.log.Error("failed to write bad request response", slog.Any("error", writeErr))
		}
		return
	}

	result, err := h.service.ExportPlan(r.Context(), req)
	if err != nil {
		statusCode := http.StatusInternalServerError
		message := "failed to export plan"

		if errors.Is(err, service.ErrInvalidUserID) ||
			errors.Is(err, service.ErrInvalidPlanID) ||
			errors.Is(err, service.ErrInvalidExportFormat) ||
			errors.Is(err, service.ErrInvalidPlanName) ||
			errors.Is(err, service.ErrEmptyExportDays) ||
			errors.Is(err, service.ErrInvalidExportDay) ||
			errors.Is(err, service.ErrEmptyExercises) ||
			errors.Is(err, service.ErrInvalidExerciseName) ||
			errors.Is(err, service.ErrInvalidExerciseSets) ||
			errors.Is(err, service.ErrInvalidExerciseReps) {
			statusCode = http.StatusBadRequest
			message = err.Error()
		}

		h.log.Error("failed to export plan",
			slog.Int("status", statusCode),
			slog.Any("error", err),
		)

		if writeErr := response.WriteError(w, statusCode, message); writeErr != nil {
			h.log.Error("failed to write error response", slog.Any("error", writeErr))
		}
		return
	}

	w.Header().Set("Content-Type", result.ContentType)
	w.Header().Set("Content-Disposition", fmt.Sprintf(`attachment; filename="%s"`, result.FileName))
	w.WriteHeader(http.StatusOK)

	if _, err := w.Write(result.Content); err != nil {
		h.log.Error("failed to write export file response", slog.Any("error", err))
	}
}
