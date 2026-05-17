import os
from dotenv import load_dotenv
from fastapi import APIRouter
from app.schemas.workout import WorkoutRequest, WorkoutResponse
from app.services.workout_service import create_workout_plan
from app.services.ai_service import AIService

load_dotenv()
router = APIRouter()
ai_service = AIService(api_key=os.getenv("GOOGLE_API_KEY"))

@router.post("/generate", response_model=WorkoutResponse)
async def generate_workout(request: WorkoutRequest):

    plan = await create_workout_plan(ai_service, request)
    return plan