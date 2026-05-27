from app.services.ai_service import AIService
from app.schemas.workout import WorkoutRequest
from loguru import logger
async def create_workout_plan(ai_service: AIService, request: WorkoutRequest):
    logger.info(f"Processing request for user_id: {request.user_id}")

    ai_content = await ai_service.generate_plan_json(request)
    
    logger.info(f"Successfully generated plan with {len(ai_content['workout_plan'])} days")
    return {
        "goal": request.goal,
        "level": request.level,
        "days_per_week": request.days_per_week,
        "split_type": request.split_type,
        "workout_plan": ai_content["workout_plan"]
    }