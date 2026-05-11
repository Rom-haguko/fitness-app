package httpserver

import (
	"net/http"

	"github.com/Rom-haguko/fitness-app/go-service/internal/handler"
)

func NewRouter() http.Handler {
	mux := http.NewServeMux()

	healthHandler := handler.NewHealthHandler()

	mux.HandleFunc("/health", healthHandler.Health)
	return mux
}
