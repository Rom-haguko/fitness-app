package handler

import (
	"context"
	"errors"
	"log/slog"
	"net/http"
	"strconv"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/Rom-haguko/fitness-app/go-service/internal/response"
	"github.com/Rom-haguko/fitness-app/go-service/internal/service"
)

type StatisticsService interface {
	GetSummary(ctx context.Context, userID int64) (dto.StatisticsSummaryResponse, error)
}

type StatisticsHandler struct {
	service StatisticsService
	log     *slog.Logger
}

func NewStatisticsHandler(service StatisticsService, log *slog.Logger) StatisticsHandler {
	return StatisticsHandler{
		service: service,
		log:     log,
	}
}

func (h StatisticsHandler) GetSummary(w http.ResponseWriter, r *http.Request) {
	if r.Method != http.MethodGet {
		if err := response.WriteError(w, http.StatusMethodNotAllowed, "method not allowed"); err != nil {
			h.log.Error("failed to write method not allowed response", slog.Any("error", err))
		}
		return
	}

	userIDRaw := r.URL.Query().Get("user_id")
	if userIDRaw == "" {
		if err := response.WriteError(w, http.StatusBadRequest, "user_id is required"); err != nil {
			h.log.Error("failed to write bad request response", slog.Any("error", err))
		}
		return
	}

	userID, err := strconv.ParseInt(userIDRaw, 10, 64)
	if err != nil {
		if writeErr := response.WriteError(w, http.StatusBadRequest, "invalid user_id"); writeErr != nil {
			h.log.Error("failed to write bad request response", slog.Any("error", writeErr))
		}
		return
	}

	summary, err := h.service.GetSummary(r.Context(), userID)
	if err != nil {
		statusCode := http.StatusInternalServerError
		message := "failed to get statistics summary"

		if errors.Is(err, service.ErrInvalidUserID) {
			statusCode = http.StatusBadRequest
			message = err.Error()
		}

		h.log.Error("failed to get statistics summary",
			slog.Int("status", statusCode),
			slog.Int64("user_id", userID),
			slog.Any("error", err),
		)

		if writeErr := response.WriteError(w, statusCode, message); writeErr != nil {
			h.log.Error("failed to write error response", slog.Any("error", writeErr))
		}
		return
	}

	if err := response.WriteJSON(w, http.StatusOK, summary); err != nil {
		h.log.Error("failed to write success response", slog.Any("error", err))
	}
}
