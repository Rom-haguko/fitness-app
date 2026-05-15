from google import genai
import json
from app.schemas.workout import WorkoutRequest
from loguru import logger

class AIService:
    def __init__(self, api_key: str):
        self.client = genai.Client(api_key=api_key)
        self.model_name = "gemini-2.5-flash-lite"

    async def generate_plan_json(self, data: WorkoutRequest):
        prompt = f"""
        Generate a workout plan in English for a {data.level} level person with the goal: {data.goal}.
        Schedule: {data.days_per_week} days per week. Split type: {data.split_type}.
        
        RETURN ONLY JSON. No markdown, no conversational text.
        Structure:
        {{
            "workout_plan": [
                {{
                    "day": 1,
                    "focus": "Focus Area",
                    "exercises": [
                        {{
                            "name": "Exercise Name",
                            "sets": 3,
                            "reps": "10-12",
                            "muscle_group": "Target Muscle",
                            "description": "Short execution guide"
                        }}
                    ]
                }}
            ]
        }}
        """
        try:
            response = self.client.models.generate_content(
                model=self.model_name,
                contents=prompt
            )
            text = response.text.strip()
            if "```json" in text:
                text = text.split("```json")[1].split("```")[0]
            elif "```" in text:
                text = text.split("```")[1].split("```")[0]
            
            return json.loads(text.strip())
        except Exception as e:
            logger.error(f"AI Service Error: {e}")
            # Fallback structure
            return {
                "workout_plan": [
                    {
                        "day": 1,
                        "focus": "Full Body Fallback",
                        "exercises": [{
                            "name": "Push-ups", "sets": 3, "reps": "15",
                            "muscle_group": "Chest", "description": "Standard push-ups"
                        }]
                    }
                ]
            }