package com.fitlat.backend.service;

import com.fitlat.backend.dto.food.CreateFoodItemRequest;
import com.fitlat.backend.dto.food.FoodItemResponse;
import com.fitlat.backend.entity.FoodCategory;
import com.fitlat.backend.entity.FoodItem;
import com.fitlat.backend.repository.FoodItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    public List<FoodItemResponse> search(
        String keyword,
        FoodCategory category
    ) {
        String normalizedKeyword =
                keyword == null || keyword.isBlank()
                        ? null
                        : keyword.trim();

        List<FoodItem> foods;

        if (normalizedKeyword == null && category == null) {
            foods = foodItemRepository.findAllByActiveTrueOrderByNameAsc();

        } else if (normalizedKeyword != null && category == null) {
            foods = foodItemRepository.searchByKeyword(normalizedKeyword);

        } else if (normalizedKeyword == null) {
            foods = foodItemRepository.searchByCategory(category.name());

        } else {
            foods = foodItemRepository.searchByKeywordAndCategory(
                    normalizedKeyword,
                    category.name()
            );
        }

        return foods.stream()
                .map(this::toResponse)
                .toList();
    }

    public FoodItemResponse getById(Long id) {
        FoodItem food = foodItemRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Food item not found"
                ));

        return toResponse(food);
    }

    @Transactional
    public FoodItemResponse create(CreateFoodItemRequest request) {
        FoodItem food = new FoodItem(
                request.name().trim(),
                request.category(),
                request.caloriesPer100g(),
                request.proteinPer100g(),
                request.carbsPer100g(),
                request.fatPer100g(),
                request.fiberPer100g()
        );

        return toResponse(foodItemRepository.save(food));
    }

    @Transactional
    public FoodItemResponse update(
            Long id,
            CreateFoodItemRequest request
    ) {
        FoodItem food = foodItemRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Food item not found"
                ));

        food.update(
                request.name().trim(),
                request.category(),
                request.caloriesPer100g(),
                request.proteinPer100g(),
                request.carbsPer100g(),
                request.fatPer100g(),
                request.fiberPer100g()
        );

        return toResponse(food);
    }

    @Transactional
    public void deactivate(Long id) {
        FoodItem food = foodItemRepository.findByIdAndActiveTrue(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Food item not found"
                ));

        food.deactivate();
    }

    private FoodItemResponse toResponse(FoodItem food) {
        return new FoodItemResponse(
                food.getId(),
                food.getName(),
                food.getCategory(),
                food.getCaloriesPer100g(),
                food.getProteinPer100g(),
                food.getCarbsPer100g(),
                food.getFatPer100g(),
                food.getFiberPer100g(),
                food.getCreatedAt(),
                food.getUpdatedAt()
        );
    }
}