package com.fitlat.backend.repository;

import com.fitlat.backend.entity.MealLogItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MealLogItemRepository extends JpaRepository<MealLogItem, Long> {

    // Lấy các thực phẩm thuộc một bữa ăn
    List<MealLogItem> findAllByMealLogIdOrderByIdAsc(Long mealLogId);

    // Tìm một thực phẩm trong bữa ăn
    Optional<MealLogItem> findByIdAndMealLogId(Long id, Long mealLogId);
}