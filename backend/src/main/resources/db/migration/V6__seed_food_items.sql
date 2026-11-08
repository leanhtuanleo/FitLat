INSERT INTO food_items (
    name, category,
    calories_per_100g,
    protein_per_100g,
    carbs_per_100g,
    fat_per_100g,
    fiber_per_100g
)
VALUES
-- GRAIN
('Cơm trắng', 'GRAIN', 130, 2.7, 28.2, 0.3, 0.4),
('Yến mạch', 'GRAIN', 389, 16.9, 66.3, 6.9, 10.6),
('Bánh mì trắng', 'GRAIN', 265, 8.9, 49.4, 3.2, 2.7),

-- MEAT
('Ức gà', 'MEAT', 165, 31.0, 0, 3.6, 0),
('Thịt bò nạc', 'MEAT', 176, 26.0, 0, 8.0, 0),
('Thịt lợn nạc', 'MEAT', 143, 21.0, 0, 5.0, 0),

-- SEAFOOD
('Cá hồi', 'SEAFOOD', 208, 20.4, 0, 13.4, 0),
('Tôm', 'SEAFOOD', 99, 24.0, 0.2, 0.3, 0),

-- FRUIT
('Chuối', 'FRUIT', 89, 1.1, 22.8, 0.3, 2.6),
('Táo', 'FRUIT', 52, 0.3, 13.8, 0.2, 2.4),
('Cam', 'FRUIT', 47, 0.9, 11.8, 0.1, 2.4),

-- VEGETABLE
('Bông cải xanh', 'VEGETABLE', 34, 2.8, 6.6, 0.4, 2.6),
('Khoai lang', 'VEGETABLE', 86, 1.6, 20.1, 0.1, 3.0),

-- DAIRY
('Sữa tươi nguyên kem', 'DAIRY', 61, 3.2, 4.8, 3.3, 0),
('Sữa chua không đường', 'DAIRY', 61, 3.5, 4.7, 3.3, 0);