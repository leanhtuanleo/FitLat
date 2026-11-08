CREATE TABLE food_items (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,

    name VARCHAR(150) NOT NULL,

    category VARCHAR(30) NOT NULL,

    calories_per_100g NUMERIC(8, 2) NOT NULL,
    protein_per_100g NUMERIC(8, 2) NOT NULL,
    carbs_per_100g NUMERIC(8, 2) NOT NULL,
    fat_per_100g NUMERIC(8, 2) NOT NULL,

    fiber_per_100g NUMERIC(8, 2),

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT chk_food_items_category CHECK (
        category IN (
            'FRUIT',
            'VEGETABLE',
            'MEAT',
            'SEAFOOD',
            'GRAIN',
            'DAIRY',
            'LEGUME',
            'NUT_SEED',
            'BEVERAGE',
            'OTHER'
        )
    ),

    CONSTRAINT chk_food_items_calories CHECK (
        calories_per_100g >= 0
    ),

    CONSTRAINT chk_food_items_protein CHECK (
        protein_per_100g >= 0
    ),

    CONSTRAINT chk_food_items_carbs CHECK (
        carbs_per_100g >= 0
    ),

    CONSTRAINT chk_food_items_fat CHECK (
        fat_per_100g >= 0
    ),

    CONSTRAINT chk_food_items_fiber CHECK (
        fiber_per_100g IS NULL OR fiber_per_100g >= 0
    )
);

CREATE INDEX idx_food_items_name
ON food_items(name);

CREATE INDEX idx_food_items_category
ON food_items(category);