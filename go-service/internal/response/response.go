package response

import (
	"encoding/json"
	"fmt"
	"net/http"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
)

func WriteJSON(w http.ResponseWriter, statusCode int, payload any) error {
	w.Header().Set("Content-type", "application/json")
	w.WriteHeader(statusCode)

	if err := json.NewEncoder(w).Encode(payload); err != nil {
		return fmt.Errorf("write json: %w", err)
	}
	return nil
}

func WriteError(w http.ResponseWriter, statusCode int, message string) error {
	return WriteJSON(w, statusCode, dto.ErrorResponse{
		Status:  "error",
		Message: message,
	})
}
