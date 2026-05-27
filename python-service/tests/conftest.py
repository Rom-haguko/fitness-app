import pytest
from fastapi.testclient import TestClient
from main import app

@pytest.fixture
def client():
    """Фикстура для создания тестового клиента FastAPI"""
    with TestClient(app) as c:
        yield c

@pytest.fixture
def mock_workout_request():
    """Данные для стандартного запроса"""
    return {
        "user_id": 1,
        "goal": "muscle_gain",
        "level": "beginner",
        "days_per_week": 3,
        "split_type": "full_body"
    }