package exporter

import (
	"bytes"
	"fmt"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
	"github.com/jung-kurt/gofpdf"
)

func BuildPDF(req dto.ExportPlanRequest) ([]byte, error) {
	pdf := gofpdf.New("P", "mm", "A4", "")
	pdf.AddPage()

	pdf.SetFont("Arial", "B", 16)
	pdf.Cell(0, 10, fmt.Sprintf("Plan: %s", req.PlanName))
	pdf.Ln(10)

	pdf.SetFont("Arial", "", 12)
	pdf.Cell(0, 8, fmt.Sprintf("User ID: %d", req.UserID))
	pdf.Ln(8)
	pdf.Cell(0, 8, fmt.Sprintf("Plan ID: %d", req.PlanID))
	pdf.Ln(12)

	for _, day := range req.Days {
		pdf.SetFont("Arial", "B", 13)
		pdf.Cell(0, 8, fmt.Sprintf("Day %d - %s", day.Day, day.Focus))
		pdf.Ln(8)

		pdf.SetFont("Arial", "", 12)

		for i, exercise := range day.Exercises {
			line := fmt.Sprintf(
				"%d. %s - %d x %s",
				i+1,
				exercise.Name,
				exercise.Sets,
				exercise.Reps,
			)

			pdf.MultiCell(0, 7, line, "", "L", false)
		}

		pdf.Ln(4)
	}

	var buf bytes.Buffer

	if err := pdf.Output(&buf); err != nil {
		return nil, fmt.Errorf("build pdf: %w", err)
	}

	return buf.Bytes(), nil
}