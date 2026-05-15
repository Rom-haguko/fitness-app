from sqlalchemy import Column, Integer, String, DateTime, ForeignKey
from app.db.database import Base
import datetime

class User(Base):
    __tablename__ = "users"
    __table_args__ = {"schema": "fitness_tracker"}
    id = Column(Integer, primary_key=True, index=True)
    username = Column(String, unique=True)
    email = Column(String, unique=True)

class WorkoutPlan(Base):
    __tablename__ = "workout_plans"
    __table_args__ = {"schema": "fitness_tracker"}

    id = Column(Integer, primary_key=True, index=True)
    user_id = Column(Integer, ForeignKey("fitness_tracker.users.id"))
    goal = Column(String)
    level = Column(String)
    days_per_week = Column(Integer)