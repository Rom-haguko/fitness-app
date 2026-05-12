package httpserver

import (
	"log/slog"
	"net/http"

	"github.com/Rom-haguko/fitness-app/go-service/internal/handler"
)

func NewRouter(
	log *slog.Logger,
	progressService handler.ProgressService,
	bodyWeightService handler.BodyWeightService,
	statisticsService handler.StatisticsService,
) http.Handler {
	mux := http.NewServeMux()

	healthHandler := handler.NewHealthHandler()
	progressHandler := handler.NewProgressHandler(progressService, log)
	bodyWeightHandler := handler.NewBodyWeightHandler(bodyWeightService, log)
	statisticsHandler := handler.NewStatisticsHandler(statisticsService, log)

	mux.HandleFunc("/health", healthHandler.Health)
	mux.HandleFunc("/api/v1/progress/logs", progressHandler.CreateProgressLog)
	mux.HandleFunc("/api/v1/body-weight", bodyWeightHandler.CreateBodyWeight)
	mux.HandleFunc("/api/v1/statistics/summary", statisticsHandler.GetSummary)
	mux.HandleFunc("/api/v1/statistics/body-weight", statisticsHandler.GetBodyWeightChart)
	mux.HandleFunc("/api/v1/statistics/volume", statisticsHandler.GetVolumeChart)

	var root http.Handler = mux
	root = RecoverMiddleware(log, root)
	root = LoggingMiddleware(log, root)

	return root
}
