package handler

import (
	"context"
	"encoding/json"
	"errors"
	"log/slog"
	"net/http"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/response"
	"github.com/Rom-haguko/fitness-app/go-service/internal/service"
)

type ProgressService interface {
	CreateProgressLog(ctx context.Context, req dto.CreateProgressLogRequest) error
}

type ProgressHandler struct {
	service ProgressService
	log     *slog.Logger
}

func NewProgressHandler(service ProgressService, log *slog.Logger) ProgressHandler {
	return ProgressHandler{
		service: service,
		log:     log,
	}
}

func (h ProgressHandler) CreateProgressLog(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		if err := response.WriteError(w, http.StatusMethodNotAllowed, "method not allowed"); err != nil {
			h.log.Error("failed to write method not allowed response", slog.Any("error", err))
		}
		return
	}

	var req dto.CreateProgressLogRequest

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		if writeErr := response.WriteError(w, http.StatusBadRequest, "invalid request body"); writeErr != nil {
			h.log.Error("failed to write bad request response", slog.Any("error", writeErr))
		}
		return
	}

	if err := h.service.CreateProgressLog(r.Context(), req); err != nil {
		statusCode := http.StatusInternalServerError
		message := "failed to create progress log"

		if errors.Is(err, service.ErrInvalidUserID) ||
			errors.Is(err, service.ErrInvalidWorkoutPlanID) ||
			errors.Is(err, service.ErrInvalidWorkoutPlanItemID) ||
			errors.Is(err, service.ErrInvalidExerciseID) ||
			errors.Is(err, service.ErrMissingExerciseReference) ||
			errors.Is(err, service.ErrInvalidSets) ||
			errors.Is(err, service.ErrInvalidReps) ||
			errors.Is(err, service.ErrInvalidWeight) {
			statusCode = http.StatusBadRequest
			message = err.Error()
		}

		h.log.Error("failed to create progress log",
			slog.Int("status", statusCode),
			slog.Any("error", err),
		)

		if writeErr := response.WriteError(w, statusCode, message); writeErr != nil {
			h.log.Error("failed to write error response", slog.Any("error", writeErr))
		}
		return
	}

	resp := dto.SuccessResponse{
		Status:  "success",
		Message: "progress log created",
	}

	if err := response.WriteJSON(w, http.StatusCreated, resp); err != nil {
		h.log.Error("failed to write success response", slog.Any("error", err))
	}
}
