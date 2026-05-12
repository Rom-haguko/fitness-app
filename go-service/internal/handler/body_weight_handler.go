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

type BodyWeightService interface {
	CreateBodyWeight(ctx context.Context, req dto.CreateBodyWeightRequest) error
}

type BodyWeightHandler struct {
	service BodyWeightService
	log     *slog.Logger
}

func NewBodyWeightHandler(service BodyWeightService, log *slog.Logger) BodyWeightHandler {
	return BodyWeightHandler{
		service: service,
		log:     log,
	}
}

func (h BodyWeightHandler) CreateBodyWeight(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodPost {
		if err := response.WriteError(w, http.StatusMethodNotAllowed, "method not allowed"); err != nil {
			h.log.Error("failed to write method not allowed response", slog.Any("error", err))
		}
		return
	}

	var req dto.CreateBodyWeightRequest

	if err := json.NewDecoder(r.Body).Decode(&req); err != nil {
		if writeErr := response.WriteError(w, http.StatusBadRequest, "invalid request body"); writeErr != nil {
			h.log.Error("failed to write bad request response", slog.Any("error", writeErr))
		}
		return
	}

	if err := h.service.CreateBodyWeight(r.Context(), req); err != nil {
		statusCode := http.StatusInternalServerError
		message := "failed to save body weight"

		if errors.Is(err, service.ErrInvalidUserID) ||
			errors.Is(err, service.ErrInvalidBodyWeight) {
			statusCode = http.StatusBadRequest
			message = err.Error()
		}

		h.log.Error("failed to save body weight",
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
		Message: "body weight saved",
	}

	if err := response.WriteJSON(w, http.StatusCreated, resp); err != nil {
		h.log.Error("failed to write success response", slog.Any("error", err))
	}
}
