# Java Service — Fitness App (BFF)

## Overview
The Java Service acts as the **Backend-for-Frontend (BFF)** and the primary orchestrator for the Fitness App project. It provides a secure user interface, handles authentication, and coordinates data flow between the User, the AI-driven Python service, and the data-focused Go service.

## Architecture
```text
[User/Browser] 
      │ (Thymeleaf/HTTP)
      ▼
┌───────────────────────────────┐
│     Java BFF Service (8080)   │ ◄───► [Python AI Service] (Generation)
│ (Auth, Orchestration, UI)     │ ◄───►[Go Progress Service] (Stats/Export)
└──────────────┬────────────────┘
               │ (JPA)
               ▼
     [PostgreSQL Database]
```

---

## Technical Stack
* **Java 17 / Spring Boot 3.4.0**
* **Template Engine:** Thymeleaf
* **Security:** Spring Security
* **Persistence:** Spring Data JPA / Hibernate
* **API Client:** WebClient (WebFlux)
* **Testing:** JUnit 5, Mockito, Testcontainers

---

## Project Structure
```text
src/main/java/com/fitness/fitnessapp/
├── config/       # Security, WebClient, JPA configurations
├── controller/   # Web request handlers (MVC)
├── dto/          # Data Transfer Objects (Auth, Plan, Progress, Export)
├── entity/       # JPA Domain Entities (User, WorkoutPlan, WorkoutPlanItem)
├── exception/    # Custom exceptions & GlobalExceptionHandler
├── logging/      # RequestLoggingFilter & LoggingUtils
├── mapper/       # Data transformation layer (Entity <-> DTO)
├── repository/   # Data access interfaces
└── service/      # Business logic & External service orchestration
```

---

## Core Responsibilities

1. **Authentication:** Managing users, sessions, and secure access via Spring Security.
2. **Orchestration:** 
   - Requests training plans from the Python Service.
   - Proxies progress logs and export requests to the Go Service.
3. **Data Management:** Persisting training plans and exercises into the PostgreSQL database.
4. **UI Rendering:** Server-side rendering of the user dashboard, plan history, and statistics pages.

---

## API & External Integrations

### 1. Plan Generation (to Python Service)
- **Endpoint:** `POST /api/v1/workout_plans/generate`
- **Purpose:** Request an AI-generated training program based on user questionnaire data.

### 2. Progress & Statistics (to Go Service)
- **POST /api/v1/progress/logs**: Record completed workouts.
- **POST /api/v1/body-weight**: Record body weight updates.
- **GET /api/v1/statistics/summary**: Fetch workout summaries and volume calculations.
- **POST /api/v1/export/plan**: Trigger PDF/TXT report generation.

---

## Testing Strategy
The project follows a layered testing approach:
- **Unit Tests:** Mockito-based tests for `Services` and `Mappers` to verify business logic and transformations.
- **Controller Tests:** `MockMvc` tests verifying request handling, validation, and security constraints.
- **Integration Tests:** `Testcontainers` (PostgreSQL) ensuring JPA mapping and database integrity.

---

## Domain Model Specification

### Entities
* **User**: Represents the application owner.
* **WorkoutPlan**: Represents a stored AI-generated plan (Goal, Level, Frequency).
* **WorkoutPlanItem**: Represents individual exercises (Sets, Reps, Focus) linked to a `WorkoutPlan`.

### Logic
* All AI interactions are asynchronous/blocking on the Java side.
* Data from Python is mapped to the relational database structure using `WorkoutPlanMapper`.
* Transactional integrity is enforced via `@Transactional` on plan generation and saving processes.

---

## Logging
The project utilizes **structured JSON logging** (Logstash Encoder) for enhanced observability. All requests are captured by the `RequestLoggingFilter`, and external calls are standardized via `LoggingUtils`, ensuring consistent keys like `user_id`, `plan_id`, and `duration_ms` for log aggregation.