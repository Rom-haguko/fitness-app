from sqlalchemy.orm import Session
from app.models.workout_plan import WorkoutPlan
from app.schemas.workout import WorkoutRequest

async def create_workout_plan(db: Session, ai_service, request: WorkoutRequest):

    ai_generated_content = await ai_service.generate_plan_json(request)
    

    db_plan = WorkoutPlan(
        user_id=request.user_id,
        goal=request.goal,
        level=request.level,
        days_per_week=request.days_per_week
    )
    db.add(db_plan)
    db.commit()


    return {
        "goal": request.goal,
        "level": request.level,
        "days_per_week": request.days_per_week,
        "split_type": request.split_type,
        "workout_plan": ai_generated_content["workout_plan"]
    }