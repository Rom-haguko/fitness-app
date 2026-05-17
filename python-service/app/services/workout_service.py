from app.services.ai_service import AIService
from app.schemas.workout import WorkoutRequest

async def create_workout_plan(ai_service: AIService, request: WorkoutRequest):

    ai_content = await ai_service.generate_plan_json(request)
    
    return {
        "goal": request.goal,
        "level": request.level,
        "days_per_week": request.days_per_week,
        "split_type": request.split_type,
        "workout_plan": ai_content["workout_plan"]
    }