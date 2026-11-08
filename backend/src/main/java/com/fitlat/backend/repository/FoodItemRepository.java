package com.fitlat.backend.repository;

import com.fitlat.backend.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    @Query(value = """
            SELECT * FROM food_items
            WHERE is_active = TRUE
            AND unaccent(LOWER(name)) LIKE
                unaccent(LOWER(CONCAT('%', :keyword, '%')))
            ORDER BY name ASC
            """, nativeQuery = true)
    List<FoodItem> searchByKeyword(@Param("keyword") String keyword);

    @Query(value = """
            SELECT * FROM food_items
            WHERE is_active = TRUE
            AND category = :category
            ORDER BY name ASC
            """, nativeQuery = true)
    List<FoodItem> searchByCategory(
            @Param("category") String category
    );

    @Query(value = """
            SELECT * FROM food_items
            WHERE is_active = TRUE
            AND unaccent(LOWER(name)) LIKE
                unaccent(LOWER(CONCAT('%', :keyword, '%')))
            AND category = :category
            ORDER BY name ASC
            """, nativeQuery = true)
    List<FoodItem> searchByKeywordAndCategory(
            @Param("keyword") String keyword,
            @Param("category") String category
    );

    List<FoodItem> findAllByActiveTrueOrderByNameAsc();

    Optional<FoodItem> findByIdAndActiveTrue(Long id);
}