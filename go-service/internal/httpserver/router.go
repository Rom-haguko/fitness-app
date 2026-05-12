package httpserver

import (
	"log/slog"
	"net/http"

	"github.com/Rom-haguko/fitness-app/go-service/internal/handler"
)

func NewRouter(log *slog.Logger) http.Handler {
	mux := http.NewServeMux()

	healthHandler := handler.NewHealthHandler()

	mux.HandleFunc("/health", healthHandler.Health)

	var root http.Handler = mux
	root = LoggingMiddleware(log, RecoverMiddleware(log, root))
	return root
}
