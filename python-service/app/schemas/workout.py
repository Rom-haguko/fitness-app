from pydantic import BaseModel
from typing import List, Optional

class WorkoutRequest(BaseModel):
    user_id: int
    goal: str
    level: str
    days_per_week: int
    split_type: Optional[str] = "full_body"

class ExerciseSchema(BaseModel):
    name: str
    sets: int
    reps: str
    muscle_group: str
    description: str

class WorkoutResponse(BaseModel):
    id: int
    goal: str
    exercises: List[ExerciseSchema]

    class Config:
        from_attributes = True