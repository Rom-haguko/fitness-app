from fastapi import FastAPI
from app.api import workout_router
from app.db.database import Base, engine

# Авто-создание таблиц (если схема fitness_tracker существует)
Base.metadata.create_all(bind=engine)

app = FastAPI(title="Fitness Python Service")

app.include_router(workout_router.router, prefix="/api/v1/workout_plans")

@app.get("/")
def health_check():
    return {"status": "Python service is running"}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="0.0.0.0", port=8001)