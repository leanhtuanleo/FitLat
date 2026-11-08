
-- 1. User profiles
CREATE TABLE user_profiles (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    user_id BIGINT NOT NULL UNIQUE,

    date_of_birth DATE,

    biological_sex VARCHAR(10),

    height_cm NUMERIC(5, 2),

    current_weight_kg NUMERIC(5, 2),

    activity_level VARCHAR(30),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_profiles_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_user_profiles_sex
        CHECK (
            biological_sex IS NULL
            OR biological_sex IN ('MALE', 'FEMALE')
        ),

    CONSTRAINT chk_user_profiles_height
        CHECK (height_cm IS NULL OR height_cm > 0),

    CONSTRAINT chk_user_profiles_weight
        CHECK (current_weight_kg IS NULL OR current_weight_kg > 0),

    CONSTRAINT chk_user_profiles_activity
        CHECK (
            activity_level IS NULL
            OR activity_level IN (
                'SEDENTARY',
                'LIGHTLY_ACTIVE',
                'MODERATELY_ACTIVE',
                'VERY_ACTIVE',
                'EXTRA_ACTIVE'
            )
        )
);

-- 2. Fitness goals
CREATE TABLE fitness_goals (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    user_id BIGINT NOT NULL,

    goal_type VARCHAR(30) NOT NULL,

    target_weight_kg NUMERIC(5, 2) NOT NULL,

    target_date DATE,

    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_fitness_goals_user
        FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE,

    CONSTRAINT chk_fitness_goals_type
        CHECK (
            goal_type IN (
                'LOSE_WEIGHT',
                'GAIN_WEIGHT',
                'MAINTAIN_WEIGHT'
            )
        ),

    CONSTRAINT chk_fitness_goals_weight
        CHECK (target_weight_kg > 0),

    CONSTRAINT chk_fitness_goals_status
        CHECK (
            status IN (
                'ACTIVE',
                'COMPLETED',
                'CANCELLED'
            )
        )
);

-- 3. Each user can have only one active goal
CREATE UNIQUE INDEX uq_fitness_goals_one_active_per_user
ON fitness_goals(user_id)
WHERE status = 'ACTIVE';

-- 4. Index for retrieving a user's goal history
CREATE INDEX idx_fitness_goals_user_id
ON fitness_goals(user_id);
