package dto

type ExportPlanRequest struct {
	UserID   int64           `json:"user_id"`
	PlanID   int64           `json:"plan_id"`
	Format   string          `json:"format"`
	PlanName string          `json:"plan_name"`
	Days     []ExportPlanDay `json:"days"`
}

type ExportPlanDay struct {
	Day       int              `json:"day"`
	Focus     string           `json:"focus"`
	Exercises []ExportExercise `json:"exercises"`
}

type ExportExercise struct {
	Name string `json:"name"`
	Sets int    `json:"sets"`
	Reps string `json:"reps"`
}
