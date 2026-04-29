from fastapi import FastAPI
from pydantic import BaseModel
from pydantic_settings import BaseSettings
import google.generativeai as genai


# api settings
class Settings(BaseSettings):
    google_api_key: str

    class Config:
        env_file = ".env"

settings = Settings()
genai.configure(api_key=settings.google_api_key)




app = FastAPI()

@app.get("/")
def read_root():
    return {"status": "Python service started!"}
