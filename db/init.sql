DROP SCHEMA IF EXISTS fitness_tracker CASCADE;
CREATE SCHEMA fitness_tracker;

SET search_path TO fitness_tracker;

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(30) NOT NULL DEFAULT 'USER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE exercises (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL UNIQUE,
    description TEXT,
    muscle_group VARCHAR(80) NOT NULL,
    equipment VARCHAR(80),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE workout_plans (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    goal VARCHAR(100) NOT NULL,
    level VARCHAR(50) NOT NULL,
    days_per_week INT NOT NULL CHECK (days_per_week BETWEEN 1 AND 7),
    split_type VARCHAR(50),
    notes TEXT,
    source VARCHAR(30) NOT NULL DEFAULT 'python-service',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE workout_plan_items (
    id BIGSERIAL PRIMARY KEY,
    workout_plan_id BIGINT NOT NULL REFERENCES workout_plans(id) ON DELETE CASCADE,
    day_number INT NOT NULL CHECK (day_number >= 1),
    focus VARCHAR(100),
    exercise_id BIGINT REFERENCES exercises(id) ON DELETE SET NULL,
    exercise_name VARCHAR(150) NOT NULL,
    sets_count INT NOT NULL CHECK (sets_count > 0),
    reps_text VARCHAR(30) NOT NULL,
    exercise_order INT NOT NULL CHECK (exercise_order > 0),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (workout_plan_id, day_number, exercise_order)
);

CREATE TABLE progress_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    workout_plan_id BIGINT NOT NULL REFERENCES workout_plans(id) ON DELETE CASCADE,
    workout_plan_item_id BIGINT REFERENCES workout_plan_items(id) ON DELETE SET NULL,
    exercise_id BIGINT REFERENCES exercises(id) ON DELETE SET NULL,
    sets INT NOT NULL CHECK (sets > 0),
    reps INT NOT NULL CHECK (reps > 0),
    weight NUMERIC(7,2) NOT NULL CHECK (weight >= 0),
    performed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CHECK (
        workout_plan_item_id IS NOT NULL
        OR exercise_id IS NOT NULL
    )
);

CREATE TABLE body_weight_logs (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    weight NUMERIC(6,2) NOT NULL CHECK (weight > 0),
    recorded_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_workout_plans_user_id_created_at
    ON workout_plans(user_id, created_at DESC);

CREATE INDEX idx_workout_plan_items_plan_day_order
    ON workout_plan_items(workout_plan_id, day_number, exercise_order);

CREATE INDEX idx_progress_logs_user_id_performed_at
    ON progress_logs(user_id, performed_at DESC);

CREATE INDEX idx_progress_logs_workout_plan_id
    ON progress_logs(workout_plan_id);

CREATE INDEX idx_progress_logs_plan_item_id
    ON progress_logs(workout_plan_item_id);

CREATE INDEX idx_body_weight_logs_user_id_recorded_at
    ON body_weight_logs(user_id, recorded_at DESC);

INSERT INTO exercises (name, description, muscle_group, equipment) VALUES
('Bench Press', 'Chest exercise using barbell or dumbbells.', 'Chest', 'Barbell'),
('Incline Dumbbell Press', 'Upper chest press movement.', 'Chest', 'Dumbbells'),
('Squat', 'Lower body compound exercise.', 'Legs', 'Barbell'),
('Leg Press', 'Machine-based leg exercise.', 'Legs', 'Machine'),
('Deadlift', 'Posterior chain compound exercise.', 'Back', 'Barbell'),
('Pull-up', 'Bodyweight pulling exercise.', 'Back', 'Pull-up Bar'),
('Bicep Curl', 'Isolation exercise for biceps.', 'Arms', 'Dumbbells');