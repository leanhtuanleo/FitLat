CREATE TABLE meal_logs (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id BIGINT NOT NULL,
    meal_type VARCHAR(20) NOT NULL,
    consumed_at TIMESTAMP NOT NULL,
    note VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_meal_logs_user
        FOREIGN KEY (user_id)
        REFERENCES users(id) ON DELETE CASCADE,

    CONSTRAINT chk_meal_logs_type
        CHECK (meal_type IN (
            'BREAKFAST', 'LUNCH', 'DINNER', 'SNACK'
        ))
);

CREATE TABLE meal_log_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    meal_log_id BIGINT NOT NULL,
    food_item_id BIGINT NOT NULL,

    food_name_snapshot VARCHAR(150) NOT NULL,

    quantity_grams NUMERIC(8, 2) NOT NULL,

    calories_per_100g NUMERIC(8, 2) NOT NULL,
    protein_per_100g NUMERIC(8, 2) NOT NULL,
    carbs_per_100g NUMERIC(8, 2) NOT NULL,
    fat_per_100g NUMERIC(8, 2) NOT NULL,
    fiber_per_100g NUMERIC(8, 2),

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_meal_items_meal
        FOREIGN KEY (meal_log_id)
        REFERENCES meal_logs(id) ON DELETE CASCADE,

    CONSTRAINT fk_meal_items_food
        FOREIGN KEY (food_item_id)
        REFERENCES food_items(id) ON DELETE RESTRICT,

    CONSTRAINT chk_meal_items_quantity
        CHECK (quantity_grams > 0),

    CONSTRAINT chk_meal_items_calories
        CHECK (calories_per_100g >= 0),

    CONSTRAINT chk_meal_items_protein
        CHECK (protein_per_100g >= 0),

    CONSTRAINT chk_meal_items_carbs
        CHECK (carbs_per_100g >= 0),

    CONSTRAINT chk_meal_items_fat
        CHECK (fat_per_100g >= 0),

    CONSTRAINT chk_meal_items_fiber
        CHECK (fiber_per_100g IS NULL OR fiber_per_100g >= 0)
);

CREATE INDEX idx_meal_logs_user_consumed
    ON meal_logs(user_id, consumed_at);

CREATE INDEX idx_meal_log_items_meal
    ON meal_log_items(meal_log_id);