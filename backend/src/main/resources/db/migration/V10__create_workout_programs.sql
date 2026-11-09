-- 1. Workout programs: Thư viện chương trình tập mẫu
CREATE TABLE workout_programs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    name_vi VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description TEXT,

    difficulty VARCHAR(20) NOT NULL
        CHECK (difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')),

    duration_weeks INTEGER NOT NULL
        CHECK (duration_weeks > 0),

    days_per_week INTEGER NOT NULL
        CHECK (days_per_week BETWEEN 1 AND 7),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Workout program exercises: Bài tập trong chương trình mẫu
CREATE TABLE workout_program_exercises (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    program_id BIGINT NOT NULL
        REFERENCES workout_programs(id) ON DELETE CASCADE,

    exercise_id BIGINT NOT NULL
        REFERENCES exercises(id) ON DELETE RESTRICT,

    day_number INTEGER NOT NULL
        CHECK (day_number BETWEEN 1 AND 7),

    order_index INTEGER NOT NULL
        CHECK (order_index > 0),

    target_sets INTEGER NOT NULL
        CHECK (target_sets > 0),

    reps_min INTEGER NOT NULL
        CHECK (reps_min > 0),

    reps_max INTEGER NOT NULL
        CHECK (reps_max >= reps_min),

    rest_seconds INTEGER NOT NULL DEFAULT 60
        CHECK (rest_seconds >= 0),

    notes VARCHAR(500),

    CONSTRAINT uq_program_day_order
        UNIQUE (program_id, day_number, order_index)
);

-- 3. User workout plans: Kế hoạch tập luyện cá nhân
CREATE TABLE user_workout_plans (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    user_id BIGINT NOT NULL
        REFERENCES users(id) ON DELETE CASCADE,

    source_program_id BIGINT
        REFERENCES workout_programs(id) ON DELETE SET NULL,

    name VARCHAR(150) NOT NULL,

    duration_weeks INTEGER NOT NULL
        CHECK (duration_weeks > 0),

    days_per_week INTEGER NOT NULL
        CHECK (days_per_week BETWEEN 1 AND 7),

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CHECK (status IN ('ACTIVE', 'PAUSED', 'COMPLETED', 'ARCHIVED')),

    start_date DATE,
    end_date DATE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_plan_dates
        CHECK (end_date IS NULL OR start_date IS NULL OR end_date >= start_date)
);

-- 4. User workout plan exercises: Bài tập trong kế hoạch cá nhân
CREATE TABLE user_workout_plan_exercises (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    plan_id BIGINT NOT NULL
        REFERENCES user_workout_plans(id) ON DELETE CASCADE,

    exercise_id BIGINT NOT NULL
        REFERENCES exercises(id) ON DELETE RESTRICT,

    day_number INTEGER NOT NULL
        CHECK (day_number BETWEEN 1 AND 7),

    order_index INTEGER NOT NULL
        CHECK (order_index > 0),

    target_sets INTEGER NOT NULL
        CHECK (target_sets > 0),

    reps_min INTEGER NOT NULL
        CHECK (reps_min > 0),

    reps_max INTEGER NOT NULL
        CHECK (reps_max >= reps_min),

    rest_seconds INTEGER NOT NULL DEFAULT 60
        CHECK (rest_seconds >= 0),

    notes VARCHAR(500),

    CONSTRAINT uq_plan_day_order
        UNIQUE (plan_id, day_number, order_index)
);

-- ============================================
-- Indexes
-- ============================================

CREATE INDEX idx_workout_programs_active
    ON workout_programs(is_active);

CREATE INDEX idx_program_exercises_program
    ON workout_program_exercises(program_id);

CREATE INDEX idx_program_exercises_exercise
    ON workout_program_exercises(exercise_id);

CREATE INDEX idx_user_workout_plans_user
    ON user_workout_plans(user_id);

CREATE INDEX idx_user_workout_plans_status
    ON user_workout_plans(user_id, status);

CREATE INDEX idx_plan_exercises_plan
    ON user_workout_plan_exercises(plan_id);

CREATE INDEX idx_plan_exercises_exercise
    ON user_workout_plan_exercises(exercise_id);