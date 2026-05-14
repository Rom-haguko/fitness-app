from pydantic import BaseModel
from typing import List, Optional

class WorkoutRequest(BaseModel):
    user_id: int
    goal: str
    level: str
    days_per_week: int
    split_type: str

class ExerciseSchema(BaseModel):
    name: str
    sets: int
    reps: str
    muscle_group: str
    description: str

class DayPlanSchema(BaseModel):
    day: int
    focus: str
    exercises: List[ExerciseSchema]

class WorkoutResponse(BaseModel):
    goal: str
    level: str
    days_per_week: int
    split_type: str
    workout_plan: List[DayPlanSchema]