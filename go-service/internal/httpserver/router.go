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
) http.Handler {
	mux := http.NewServeMux()

	healthHandler := handler.NewHealthHandler()
	progressHandler := handler.NewProgressHandler(progressService, log)
	bodyWeightHandler := handler.NewBodyWeightHandler(bodyWeightService, log)

	mux.HandleFunc("/health", healthHandler.Health)
	mux.HandleFunc("/api/v1/progress/logs", progressHandler.CreateProgressLog)
	mux.HandleFunc("/api/v1/body-weight", bodyWeightHandler.CreateBodyWeight)

	var root http.Handler = mux
	root = RecoverMiddleware(log, root)
	root = LoggingMiddleware(log, root)

	return root
}