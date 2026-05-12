package exporter

import (
	"bytes"
	"fmt"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
)

func BuildTXT(req dto.ExportPlanRequest) ([]byte, error) {
	var buf bytes.Buffer

	if _, err := fmt.Fprintf(&buf, "Plan: %s\n", req.PlanName); err != nil {
		return nil, fmt.Errorf("write plan name: %w", err)
	}

	if _, err := fmt.Fprintf(&buf, "User ID: %d\n", req.UserID); err != nil {
		return nil, fmt.Errorf("write user id: %w", err)
	}

	if _, err := fmt.Fprintf(&buf, "Plan ID: %d\n\n", req.PlanID); err != nil {
		return nil, fmt.Errorf("write plan id: %w", err)
	}

	for _, day := range req.Days {
		if _, err := fmt.Fprintf(&buf, "Day %d - %s\n", day.Day, day.Focus); err != nil {
			return nil, fmt.Errorf("write day: %w", err)
		}

		for i, exercise := range day.Exercises {
			if _, err := fmt.Fprintf(
				&buf,
				"%d. %s — %d x %s\n",
				i+1,
				exercise.Name,
				exercise.Sets,
				exercise.Reps,
			); err != nil {
				return nil, fmt.Errorf("write exercise: %w", err)
			}
		}

		if _, err := fmt.Fprintln(&buf); err != nil {
			return nil, fmt.Errorf("write empty line: %w", err)
		}
	}

	return buf.Bytes(), nil
}