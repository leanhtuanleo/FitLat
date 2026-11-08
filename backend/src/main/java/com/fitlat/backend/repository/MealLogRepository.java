package com.fitlat.backend.repository;

import com.fitlat.backend.entity.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {

    // Lấy danh sách bữa ăn của user trong khoảng thời gian
    List<MealLog> findAllByUserIdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtAsc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Tìm bữa ăn theo ID và user sở hữu
    Optional<MealLog> findByIdAndUserId(Long id, Long userId);
}