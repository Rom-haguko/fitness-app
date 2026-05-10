from sqlalchemy import Column, Integer, String, DateTime
from app.db.database import Base
import datetime

class WorkoutPlan(Base):
    __tablename__ = "workout_plans"
    __table_args__ = {"schema": "fitness_tracker"}

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer)
    goal = Column(String)
    level = Column(String)
    days_per_week = Column(Integer)
    # start_date = Column(DateTime, default=datetime.datetime.utcnow)

class Exercise(Base):
    __tablename__ = "exercises"
    __table_args__ = {"schema": "fitness_tracker"}

    id = Column(Integer, primary_key=True, index=True)
    name = Column(String)
    description = Column(String)
    muscle_group = Column(String)