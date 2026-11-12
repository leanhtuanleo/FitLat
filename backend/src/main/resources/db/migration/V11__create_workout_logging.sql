-- 1. Workout sessions
CREATE TABLE workout_sessions (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    user_id BIGINT NOT NULL,
    workout_plan_id BIGINT,

    session_date DATE NOT NULL DEFAULT CURRENT_DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',

    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP,
    duration_minutes INTEGER,

    notes VARCHAR(1000),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_workout_sessions_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_workout_sessions_plan
        FOREIGN KEY (workout_plan_id)
        REFERENCES user_workout_plans(id)
        ON DELETE SET NULL,

    CONSTRAINT chk_workout_session_status
        CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'CANCELLED')),

    CONSTRAINT chk_workout_session_duration
        CHECK (duration_minutes IS NULL OR duration_minutes >= 0),

    CONSTRAINT chk_workout_session_completed_at
        CHECK (completed_at IS NULL OR completed_at >= started_at)
);

CREATE INDEX idx_workout_sessions_user_date
    ON workout_sessions(user_id, session_date DESC);

CREATE INDEX idx_workout_sessions_plan
    ON workout_sessions(workout_plan_id);


-- 2. Exercises performed in each session
CREATE TABLE workout_session_exercises (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    session_id BIGINT NOT NULL,
    exercise_id BIGINT NOT NULL,
    plan_exercise_id BIGINT,

    exercise_name_vi VARCHAR(150) NOT NULL,
    exercise_name_en VARCHAR(150) NOT NULL,

    order_index INTEGER NOT NULL,

    target_sets INTEGER,
    target_reps_min INTEGER,
    target_reps_max INTEGER,
    rest_seconds INTEGER,

    notes VARCHAR(1000),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_session_exercises_session
        FOREIGN KEY (session_id)
        REFERENCES workout_sessions(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_session_exercises_exercise
        FOREIGN KEY (exercise_id)
        REFERENCES exercises(id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_session_exercises_plan_exercise
        FOREIGN KEY (plan_exercise_id)
        REFERENCES user_workout_plan_exercises(id)
        ON DELETE SET NULL,

    CONSTRAINT uq_session_exercise_order
        UNIQUE (session_id, order_index),

    CONSTRAINT chk_session_exercise_order
        CHECK (order_index > 0),

    CONSTRAINT chk_session_target_sets
        CHECK (target_sets IS NULL OR target_sets > 0),

    CONSTRAINT chk_session_target_reps
        CHECK (
            (target_reps_min IS NULL AND target_reps_max IS NULL)
            OR
            (
                target_reps_min > 0
                AND target_reps_max >= target_reps_min
            )
        ),

    CONSTRAINT chk_session_rest_seconds
        CHECK (rest_seconds IS NULL OR rest_seconds >= 0)
);

CREATE INDEX idx_session_exercises_session
    ON workout_session_exercises(session_id);


-- 3. Actual set logs
CREATE TABLE workout_set_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    session_exercise_id BIGINT NOT NULL,

    set_number INTEGER NOT NULL,

    reps INTEGER,
    weight_kg NUMERIC(7,2),
    duration_seconds INTEGER,
    distance_meters NUMERIC(10,2),

    rpe NUMERIC(3,1),
    notes VARCHAR(500),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_workout_set_logs_exercise
        FOREIGN KEY (session_exercise_id)
        REFERENCES workout_session_exercises(id)
        ON DELETE CASCADE,

    CONSTRAINT uq_workout_set_number
        UNIQUE (session_exercise_id, set_number),

    CONSTRAINT chk_workout_set_number
        CHECK (set_number > 0),

    CONSTRAINT chk_workout_set_reps
        CHECK (reps IS NULL OR reps >= 0),

    CONSTRAINT chk_workout_set_weight
        CHECK (weight_kg IS NULL OR weight_kg >= 0),

    CONSTRAINT chk_workout_set_duration
        CHECK (duration_seconds IS NULL OR duration_seconds >= 0),

    CONSTRAINT chk_workout_set_distance
        CHECK (distance_meters IS NULL OR distance_meters >= 0),

    CONSTRAINT chk_workout_set_rpe
        CHECK (rpe IS NULL OR (rpe >= 1 AND rpe <= 10)),

    CONSTRAINT chk_workout_set_has_measurement
        CHECK (
            reps IS NOT NULL
            OR duration_seconds IS NOT NULL
            OR distance_meters IS NOT NULL
        )
);

CREATE INDEX idx_workout_set_logs_session_exercise
    ON workout_set_logs(session_exercise_id);