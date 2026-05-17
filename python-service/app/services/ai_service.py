from google import genai
import json
from app.schemas.workout import WorkoutRequest
from loguru import logger

class AIService:
    def __init__(self, api_key: str):
        self.client = genai.Client(api_key=api_key)
        self.model_name = "gemini-2.5-flash"

    async def generate_plan_json(self, data: WorkoutRequest):

        prompt = f"""
        Act as a professional high-end fitness coach. 
        Create a comprehensive workout plan for a {data.level} level athlete.
        Goal: {data.goal}.
        Days per week: {data.days_per_week}.
        Split type: {data.split_type}.

        STRICT REQUIREMENTS:
        1. Generate exactly {data.days_per_week} separate workout days.
        2. For EACH day, provide MINIMUM 6 different exercises.
        3. Use progressive overload principles.
        4. ALL text must be in English.
        5. RETURN ONLY VALID JSON. No preamble, no comments.

        JSON STRUCTURE:
        {{
            "workout_plan": [
                {{
                    "day": 1,
                    "focus": "Muscle Group Focus",
                    "exercises": [
                        {{
                            "name": "Exercise Name",
                            "sets": 4,
                            "reps": "8-12",
                            "muscle_group": "Target Muscle",
                            "description": "Full technique description"
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

            return self._get_fallback_plan(data.days_per_week)

    def _get_fallback_plan(self, days):
        """Метод для возврата базового плана, если ИИ не ответил"""
        plan = []
        for d in range(1, days + 1):
            plan.append({
                "day": d,
                "focus": "Full Body Base",
                "exercises": [
                    {"name": "Push-ups", "sets": 3, "reps": "15", "muscle_group": "Chest", "description": "Standard push-ups"},
                    {"name": "Bodyweight Squats", "sets": 3, "reps": "20", "muscle_group": "Legs", "description": "Classic squats"},
                    {"name": "Plank", "sets": 3, "reps": "45 sec", "muscle_group": "Core", "description": "Hold straight position"}
                ]
            })
        return {"workout_plan": plan}