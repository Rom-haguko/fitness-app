CREATE SCHEMA IF NOT EXISTS fitness_tracker;

CREATE TABLE IF NOT EXISTS fitness_tracker.users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fitness_tracker.workout_plans (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES fitness_tracker.users(id),
    goal VARCHAR(100),
    level VARCHAR(50),
    days_per_week INT,
    start_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fitness_tracker.exercises (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100),
    description TEXT,
    muscle_group VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS fitness_tracker.progress_logs (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES fitness_tracker.users(id),
    workout_plan_id INT REFERENCES fitness_tracker.workout_plans(id),
    exercise_id INT REFERENCES fitness_tracker.exercises(id),
    sets INT,
    reps INT,
    weight INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS fitness_tracker.body_weight_logs (
    id SERIAL PRIMARY KEY,
    user_id INT REFERENCES fitness_tracker.users(id),
    weight INT,
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO fitness_tracker.users (username, password_hash, email)
VALUES
    ('john_doe', 'hashed_password_1', 'john@example.com'),
    ('jane_smith', 'hashed_password_2', 'jane@example.com');

INSERT INTO fitness_tracker.exercises (name, description, muscle_group)
VALUES
    ('Bench Press', 'Chest exercise using a barbell or dumbbells.', 'Chest'),
    ('Squat', 'Lower body exercise to strengthen legs and glutes.', 'Legs'),
    ('Deadlift', 'Full-body exercise focusing on back, legs, and core.', 'Back'),
    ('Pull-up', 'Upper body exercise targeting the back and arms.', 'Back'),
    ('Bicep Curl', 'Arm exercise to target biceps using dumbbells.', 'Arms');

INSERT INTO fitness_tracker.workout_plans (user_id, goal, level, days_per_week)
VALUES
    (1, 'Weight Loss', 'Beginner', 3),
    (2, 'Muscle Gain', 'Intermediate', 4);

INSERT INTO fitness_tracker.progress_logs (user_id, workout_plan_id, exercise_id, sets, reps, weight)
VALUES
    (1, 1, 1, 4, 10, 60),
    (1, 1, 2, 3, 12, 80),
    (1, 1, 3, 4, 8, 100);

INSERT INTO fitness_tracker.progress_logs (user_id, workout_plan_id, exercise_id, sets, reps, weight)
VALUES
    (2, 2, 1, 4, 8, 80),
    (2, 2, 2, 4, 10, 100),
    (2, 2, 4, 4, 10, 40);

INSERT INTO fitness_tracker.body_weight_logs (user_id, weight)
VALUES
    (1, 80),
    (2, 65);
