package service

import (
	"context"
	"errors"
	"strings"
	"testing"

	"github.com/Rom-haguko/fitness-app/go-service/internal/dto"
)

func TestExportService_ExportPlan_TXTSuccess(t *testing.T) {
	t.Parallel()

	svc := NewExportService()

	result, err := svc.ExportPlan(context.Background(), validExportPlanRequest())
	if err != nil {
		t.Fatalf("expected nil error, got %v", err)
	}

	if result.ContentType != "text/plain; charset=utf-8" {
		t.Fatalf("expected content type text/plain; charset=utf-8, got %s", result.ContentType)
	}

	if result.FileName != "plan_1.txt" {
		t.Fatalf("expected filename plan_1.txt, got %s", result.FileName)
	}

	content := string(result.Content)

	if !strings.Contains(content, "Muscle Gain Plan") {
		t.Fatalf("expected content to contain plan name, got %s", content)
	}

	if !strings.Contains(content, "Day 1 - Full Body") {
		t.Fatalf("expected content to contain day, got %s", content)
	}

	if !strings.Contains(content, "Bench Press") {
		t.Fatalf("expected content to contain exercise, got %s", content)
	}
}

func TestExportService_ExportPlan_ValidationErrors(t *testing.T) {
	t.Parallel()

	tests := []struct {
		name        string
		request     dto.ExportPlanRequest
		expectedErr error
	}{
		{
			name: "invalid user id",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.UserID = 0
				return req
			}(),
			expectedErr: ErrInvalidUserID,
		},
		{
			name: "invalid plan id",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.PlanID = 0
				return req
			}(),
			expectedErr: ErrInvalidPlanID,
		},
		{
			name: "invalid format",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.Format = "docx"
				return req
			}(),
			expectedErr: ErrInvalidExportFormat,
		},
		{
			name: "empty plan name",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.PlanName = ""
				return req
			}(),
			expectedErr: ErrInvalidPlanName,
		},
		{
			name: "empty days",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.Days = nil
				return req
			}(),
			expectedErr: ErrEmptyExportDays,
		},
		{
			name: "invalid day",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.Days[0].Day = 0
				return req
			}(),
			expectedErr: ErrInvalidExportDay,
		},
		{
			name: "empty exercises",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.Days[0].Exercises = nil
				return req
			}(),
			expectedErr: ErrEmptyExercises,
		},
		{
			name: "invalid exercise name",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.Days[0].Exercises[0].Name = ""
				return req
			}(),
			expectedErr: ErrInvalidExerciseName,
		},
		{
			name: "invalid exercise sets",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.Days[0].Exercises[0].Sets = 0
				return req
			}(),
			expectedErr: ErrInvalidExerciseSets,
		},
		{
			name: "invalid exercise reps",
			request: func() dto.ExportPlanRequest {
				req := validExportPlanRequest()
				req.Days[0].Exercises[0].Reps = ""
				return req
			}(),
			expectedErr: ErrInvalidExerciseReps,
		},
	}

	svc := NewExportService()

	for _, tt := range tests {
		tt := tt

		t.Run(tt.name, func(t *testing.T) {
			t.Parallel()

			_, err := svc.ExportPlan(context.Background(), tt.request)
			if !errors.Is(err, tt.expectedErr) {
				t.Fatalf("expected error %v, got %v", tt.expectedErr, err)
			}
		})
	}
}

func validExportPlanRequest() dto.ExportPlanRequest {
	return dto.ExportPlanRequest{
		UserID:   1,
		PlanID:   1,
		Format:   "txt",
		PlanName: "Muscle Gain Plan",
		Days: []dto.ExportPlanDay{
			{
				Day:   1,
				Focus: "Full Body",
				Exercises: []dto.ExportExercise{
					{
						Name: "Bench Press",
						Sets: 4,
						Reps: "8-10",
					},
				},
			},
		},
	}
}

func TestExportService_ExportPlan_PDFSuccess(t *testing.T) {
	t.Parallel()

	req := validExportPlanRequest()
	req.Format = "pdf"

	svc := NewExportService()

	result, err := svc.ExportPlan(context.Background(), req)
	if err != nil {
		t.Fatalf("expected nil error, got %v", err)
	}

	if result.ContentType != "application/pdf" {
		t.Fatalf("expected content type application/pdf, got %s", result.ContentType)
	}

	if result.FileName != "plan_1.pdf" {
		t.Fatalf("expected filename plan_1.pdf, got %s", result.FileName)
	}

	if len(result.Content) == 0 {
		t.Fatal("expected non-empty pdf content")
	}
}
