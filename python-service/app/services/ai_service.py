from google import genai
import json
from app.schemas.workout import WorkoutRequest
from loguru import logger

class AIService:
    def __init__(self, api_key: str):
        # Инициализация нового клиента
        self.client = genai.Client(api_key=api_key)
        self.model_name = "gemini-2.5-flash" # Рекомендуемая быстрая модель

    async def generate_plan_json(self, data: WorkoutRequest):
        # Мы перевели инструкцию на русский и явно попросили использовать русский язык для контента
        prompt = f"""
        Сгенерируй подробный тренировочный план для человека с уровнем подготовки "{data.level}" и целью "{data.goal}".
        Количество тренировок в неделю: {data.days_per_week}.
        
        ОТВЕТЬ ТОЛЬКО В ФОРМАТЕ JSON. 
        Все значения в полях "name", "muscle_group" и "description" должны быть НА РУССКОМ ЯЗЫКЕ.
        
        Формат ответа: 
        [{{ 
            "name": "Название упражнения", 
            "sets": 3, 
            "reps": "12", 
            "muscle_group": "Группа мышц", 
            "description": "Краткое описание техники выполнения" 
        }}]
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
            
            text = text.strip()
            from loguru import logger
            logger.info(f"AI response received in Russian")
            
            return json.loads(text)
            
        except Exception as e:
            from loguru import logger
            logger.error(f"Error AI Service: {e}")

            return [{
                "name": "Отжимания", 
                "sets": 3, 
                "reps": "15", 
                "muscle_group": "Грудь", 
                "description": "Классические отжимания от пола"
            }]