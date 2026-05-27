# Fitness AI Generation Service (Python)

This service acts as the core "AI Brain" of the Fitness Tracker application. It leverages Large Language Models to transform user profile data into structured, professional workout routines.

## Key Features

*   **AI-Powered Generation**: Integrated with **Google Gemini 1.5 Flash** for high-quality, personalized fitness plans.
*   **Stateless Architecture**: Designed as a pure functional service without a local database, adhering to the **BFF (Backend-for-Frontend)** architectural pattern.
*   **Database Compatibility**: Implements strict prompt engineering to enforce character limits (e.g., 30-char limit for exercise names) ensuring seamless integration with the shared PostgreSQL schema.
*   **Resiliency & Fallbacks**: Built-in error handling that provides a high-quality "baseline" workout plan if the AI service is unavailable or restricted by region.
*   **Structured Logging**: Production-ready JSON logging using **Loguru**, compatible with ELK stack and centralized monitoring systems.

## Tech Stack

*   **Framework**: FastAPI (Asynchronous, high-performance)
*   **AI SDK**: Google GenAI
*   **Data Validation**: Pydantic v2
*   **Logging**: Loguru (JSON serialized)
*   **Testing**: Pytest with asynchronous mocking

## Architecture Role

In our microservice ecosystem:
1.  **Java Service** orchestrates the request and handles authentication.
2.  **Java Service** sends a JSON request to this **Python Service**.
3.  **Python Service** queries the Gemini API, cleans the data, and returns a structured JSON plan.
4.  **Java Service** persists the final data into the **PostgreSQL** database.

## Installation & Running

### Running with Docker (Recommended)
This service is designed to run as part of the multi-container infrastructure. From the project root directory, run:

```bash
docker-compose up --build
```
*The AI service will be reachable by other containers at `http://python-service:8001`.*

### Local Development (Optional)
If you need to debug the Python service individually:

1. **Environment**: Create a `.env` file in `/python-service` with your `GOOGLE_API_KEY`.
2. **Setup & Run**:
   ```bash
   python3 -m venv .venv
   source .venv/bin/activate
   pip install -r requirements.txt
   python3 main.py
   ```

## API Documentation

Once the service is running, visit:
*   **Swagger UI**: `http://localhost:8001/docs`
*   **ReDoc**: `http://localhost:8001/redoc`

## Testing

The project uses `pytest` with mocking to ensure reliability without consuming AI API quotas. It is recommended to run tests before pushing changes:

```bash
python3 -m pytest
```

## Regional Availability Note

Google Gemini API may have regional restrictions (e.g., error `400 FAILED_PRECONDITION`). Ensure your environment uses a supported region via VPN if necessary. The service will automatically provide a **Fallback Plan** in such cases to ensure the application remains functional.