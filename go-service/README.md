# Go Service — Fitness App

## Обзор

Go-service — отдельный backend-микросервис проекта Fitness App.

Сервис отвечает за:

* сохранение прогресса тренировок;
* сохранение веса пользователя;
* расчёт тренировочной статистики;
* подготовку данных для графиков;
* экспорт тренировочных планов в TXT/PDF;
* работу с PostgreSQL;
* REST API для Java-service.

Архитектура проекта:

```text
Java Service  ->  Go Service
Python Service -> Java Service

Go Service -> PostgreSQL
Java Service -> PostgreSQL
Python Service -> PostgreSQL
```

Go-service НЕ взаимодействует напрямую с Python-service.
Go-service получает подготовленные запросы от Java-service и работает с PostgreSQL.

---

# Зона ответственности

Go-service отвечает за:

* сохранение логов тренировок;
* сохранение веса пользователя;
* расчёт тренировочной статистики;
* подготовку данных для графиков;
* экспорт тренировочных планов;
* REST API для Java-service.


# Используемые технологии

* Go 1.22
* PostgreSQL
* pgx/v5
* Docker
* slog
* gofpdf

---

# Структура проекта

```text
cmd/api/
    main.go

internal/
    app/
    config/
    db/
    dto/
    exporter/
    handler/
    httpserver/
    logger/
    model/
    repository/
    response/
    service/
```

---

# Описание пакетов

## cmd/api

Точка входа приложения.

Задачи:

* загрузка конфигурации;
* создание logger;
* создание app;
* graceful shutdown.

Главный файл:

```text
cmd/api/main.go
```

---

## internal/app

Слой сборки приложения.

Задачи:

* связывание зависимостей;
* создание repository;
* создание service;
* создание router;
* настройка HTTP server.

Главный файл:

```text
internal/app/app.go
```

---

## internal/config

Пакет конфигурации окружения.

Задачи:

* загрузка environment variables;
* хранение config;
* генерация DSN;
* генерация server address.

Главный файл:

```text
internal/config/config.go
```

---

## internal/db

Пакет подключения к PostgreSQL.

Задачи:

* инициализация pgxpool;
* retry логика подключения;
* настройка connection pool.

Главный файл:

```text
internal/db/postgres.go
```

---

## internal/logger

Пакет structured logging.

Задачи:

* настройка slog;
* JSON logging.

Главный файл:

```text
internal/logger/logger.go
```

---

## internal/httpserver

HTTP infrastructure.

Задачи:

* настройка router;
* регистрация middleware;
* request logging;
* panic recovery.

Файлы:

```text
router.go
middleware.go
```

---

## internal/response

Пакет helper-функций для HTTP ответов.

Задачи:

* JSON serialization;
* error response formatting.

Главный файл:

```text
internal/response/response.go
```

---

## internal/dto

DTO слой.

Задачи:

* request structures;
* response structures;
* transport-level модели.

Примеры:

```text
CreateProgressLogRequest
StatisticsSummaryResponse
ExportPlanRequest
```

DTO используются только для HTTP/API взаимодействия.

---

## internal/model

Domain model слой.

Задачи:

* внутренние модели приложения;
* модели для работы с БД;
* бизнес-структуры.

Примеры:

```text
ProgressLog
BodyWeightLog
StatisticsSummary
```

Model НЕ используются напрямую в HTTP API.

---

## internal/repository

Слой работы с PostgreSQL.

Задачи:

* SQL запросы;
* INSERT/SELECT операции;
* работа с pgx.

Repository не содержит бизнес-логики.

Примеры:

```text
ProgressRepository
BodyWeightRepository
StatisticsRepository
```

---

## internal/service

Business logic слой.

Задачи:

* валидация данных;
* orchestration;
* бизнес-правила;
* взаимодействие между handler и repository.

Примеры:

```text
ProgressService
StatisticsService
ExportService
```

Service НЕ работает напрямую с HTTP.

---

## internal/handler

HTTP handler слой.

Задачи:

* обработка HTTP requests;
* чтение JSON;
* вызов service;
* возврат HTTP responses.

Примеры:

```text
ProgressHandler
StatisticsHandler
ExportHandler
```

---

## internal/exporter

Пакет генерации файлов.

Задачи:

* TXT export;
* PDF export.

Файлы:

```text
txt_exporter.go
pdf_exporter.go
```

---

# Реализованные endpoint'ы

## Health Check

```http
GET /health
```

Ответ:

```json
{
  "status": "ok"
}
```

---

## Создание progress log

```http
POST /api/v1/progress/logs
```

Request:

```json
{
  "user_id": 1,
  "workout_plan_id": 1,
  "exercise_id": 1,
  "sets": 4,
  "reps": 10,
  "weight": 60.5
}
```

---

## Сохранение веса пользователя

```http
POST /api/v1/body-weight
```

Request:

```json
{
  "user_id": 1,
  "weight": 79.5
}
```

---

## Summary статистика

```http
GET /api/v1/statistics/summary?user_id=1
```

Response:

```json
{
  "user_id": 1,
  "total_workouts": 1,
  "last_workout_date": "2026-05-12T06:00:00Z",
  "current_body_weight": 79.5,
  "total_volume": 2420
}
```

---

## График веса

```http
GET /api/v1/statistics/body-weight?user_id=1
```

Response:

```json
{
  "user_id": 1,
  "points": [
    {
      "date": "2026-05-12",
      "weight": 79.5
    }
  ]
}
```

---

## График объёма тренировок

```http
GET /api/v1/statistics/volume?user_id=1
```

Response:

```json
{
  "user_id": 1,
  "points": [
    {
      "date": "2026-05-12",
      "volume": 2420
    }
  ]
}
```

---

## Экспорт тренировочного плана

```http
POST /api/v1/export/plan
```

Request:

```json
{
  "user_id": 1,
  "plan_id": 1,
  "format": "pdf",
  "plan_name": "Muscle Gain Plan",
  "days": [
    {
      "day": 1,
      "focus": "Full Body",
      "exercises": [
        {
          "name": "Bench Press",
          "sets": 4,
          "reps": "8-10"
        }
      ]
    }
  ]
}
```

Поддерживаемые форматы:

* txt
* pdf

---

# Логирование

Используется structured JSON logging через slog.

Пример:

```json
{
  "time":"2026-05-12T06:03:27Z",
  "level":"INFO",
  "msg":"http request",
  "method":"GET",
  "path":"/health",
  "status":200
}
```

---

# Тестирование

Используются unit tests для service слоя.

Запуск:

```bash
go test ./...
```

---

# Docker

Запуск проекта:

```bash
docker compose up --build
```

Остановка:

```bash
docker compose down
```

Полная очистка volume:

```bash
docker compose down -v
```

---

# PostgreSQL

Используется PostgreSQL + pgxpool.

Основные таблицы:

```text
users
workout_plans
workout_plan_items
progress_logs
body_weight_logs
```

---

# Архитурный подход

Проект реализован по layered architecture:

```text
handler -> service -> repository -> PostgreSQL
```

Принципы:

* separation of concerns;
* dependency injection;
* DTO/model separation;
* structured logging;
* graceful shutdown;
* unit testing;
* idiomatic Go code.
