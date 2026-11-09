-- 1. Muscle groups
CREATE TABLE muscle_groups (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name_vi VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 2. Equipment
CREATE TABLE equipment (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name_vi VARCHAR(100) NOT NULL,
    name_en VARCHAR(100) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE
);

-- 3. Exercises
CREATE TABLE exercises (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name_vi VARCHAR(150) NOT NULL,
    name_en VARCHAR(150) NOT NULL,
    description TEXT,
    instructions TEXT,
    difficulty VARCHAR(20) NOT NULL DEFAULT 'BEGINNER',
    video_url VARCHAR(500),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_exercises_difficulty CHECK (
        difficulty IN ('BEGINNER', 'INTERMEDIATE', 'ADVANCED')
    )
);

-- 4. Exercise - Muscle group
CREATE TABLE exercise_muscle_groups (
    exercise_id BIGINT NOT NULL,
    muscle_group_id BIGINT NOT NULL,
    muscle_role VARCHAR(20) NOT NULL,

    PRIMARY KEY (exercise_id, muscle_group_id),

    CONSTRAINT fk_emg_exercise FOREIGN KEY (exercise_id)
        REFERENCES exercises(id) ON DELETE CASCADE,

    CONSTRAINT fk_emg_muscle_group FOREIGN KEY (muscle_group_id)
        REFERENCES muscle_groups(id) ON DELETE RESTRICT,

    CONSTRAINT chk_emg_role CHECK (
        muscle_role IN ('PRIMARY', 'SECONDARY')
    )
);

-- 5. Exercise - Equipment
CREATE TABLE exercise_equipment (
    exercise_id BIGINT NOT NULL,
    equipment_id BIGINT NOT NULL,

    PRIMARY KEY (exercise_id, equipment_id),

    CONSTRAINT fk_ee_exercise FOREIGN KEY (exercise_id)
        REFERENCES exercises(id) ON DELETE CASCADE,

    CONSTRAINT fk_ee_equipment FOREIGN KEY (equipment_id)
        REFERENCES equipment(id) ON DELETE RESTRICT
);

CREATE INDEX idx_exercises_name_vi ON exercises(name_vi);
CREATE INDEX idx_exercises_name_en ON exercises(name_en);
CREATE INDEX idx_exercises_difficulty ON exercises(difficulty);
CREATE INDEX idx_emg_muscle_group ON exercise_muscle_groups(muscle_group_id);
CREATE INDEX idx_ee_equipment ON exercise_equipment(equipment_id);

-- 6. Seed muscle groups
INSERT INTO muscle_groups (code, name_vi, name_en) VALUES
('CHEST', 'Ngực', 'Chest'),
('BACK', 'Lưng', 'Back'),
('SHOULDERS', 'Vai', 'Shoulders'),
('BICEPS', 'Tay trước', 'Biceps'),
('TRICEPS', 'Tay sau', 'Triceps'),
('ABS', 'Cơ bụng', 'Abs'),
('QUADRICEPS', 'Cơ đùi trước', 'Quadriceps'),
('HAMSTRINGS', 'Cơ đùi sau', 'Hamstrings'),
('GLUTES', 'Cơ mông', 'Glutes'),
('CALVES', 'Bắp chân', 'Calves'),
('FOREARMS', 'Cẳng tay', 'Forearms');

-- 7. Seed equipment
INSERT INTO equipment (code, name_vi, name_en) VALUES
('BODYWEIGHT', 'Trọng lượng cơ thể', 'Bodyweight'),
('DUMBBELL', 'Tạ đơn', 'Dumbbell'),
('BARBELL', 'Tạ đòn', 'Barbell'),
('MACHINE', 'Máy tập', 'Machine'),
('CABLE', 'Máy cáp', 'Cable'),
('KETTLEBELL', 'Tạ ấm', 'Kettlebell'),
('RESISTANCE_BAND', 'Dây kháng lực', 'Resistance Band'),
('BENCH', 'Ghế tập', 'Bench'),
('PULL_UP_BAR', 'Xà đơn', 'Pull-up Bar');
