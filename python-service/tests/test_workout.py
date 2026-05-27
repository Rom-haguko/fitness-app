import pytest
from unittest.mock import MagicMock, patch

def test_health_check(client):
    """Проверяем, что сервис просто дышит"""
    response = client.get("/")
    assert response.status_code == 200
    assert response.json() == {"status": "Python service is running"}

def test_generate_workout_success(client, mock_workout_request):
    """Тест успешной генерации плана (с подменой ответа ИИ)"""
    
    mock_ai_response = MagicMock()
    mock_ai_response.text = """
    {
        "workout_plan": [
            {
                "day": 1,
                "focus": "Test Focus",
                "exercises": [
                    {"name": "Test Ex", "sets": 3, "reps": "10", "muscle_group": "None", "description": "Desc"}
                ]
            }
        ]
    }
    """

    with patch("google.genai.models.Models.generate_content", return_value=mock_ai_response):
        response = client.post("/api/v1/workout_plans/generate", json=mock_workout_request)
        
        assert response.status_code == 200
        data = response.json()
        assert "workout_plan" in data
        assert data["goal"] == mock_workout_request["goal"]
        assert len(data["workout_plan"]) == 1

def test_generate_workout_fallback(client, mock_workout_request):
    """Тест того, что сервис выдает Fallback-план, если ИИ упал"""
    
    with patch("google.genai.models.Models.generate_content", side_effect=Exception("AI limit reached")):
        response = client.post("/api/v1/workout_plans/generate", json=mock_workout_request)
        
        assert response.status_code == 200
        data = response.json()
        # Срабатывание _get_fallback_plan
        assert data["workout_plan"][0]["focus"] == "Full Body Base"