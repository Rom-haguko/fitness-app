from sqlalchemy.orm import Session
from app.models.workout_plan import WorkoutPlan
from app.services.ai_service import AIService
from app.schemas.workout import WorkoutRequest

async def create_workout_plan(db: Session, ai_service: AIService, request: WorkoutRequest):
    # 1. Генерация упражнения через ИИ
    exercises_data = await ai_service.generate_plan_json(request)
    

    db_plan = WorkoutPlan(
        user_id=request.user_id,
        goal=request.goal,
        level=request.level,
        days_per_week=request.days_per_week
    )
    
    db.add(db_plan)
    db.commit()
    db.refresh(db_plan)

    # 3. Формирование финального объекта
    return {
        "id": db_plan.id,
        "goal": db_plan.goal,
        "exercises": exercises_data # Упражнения берем из ответа ИИ
    }