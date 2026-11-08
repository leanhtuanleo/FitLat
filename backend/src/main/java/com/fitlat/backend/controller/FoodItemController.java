package com.fitlat.backend.controller;

import com.fitlat.backend.dto.food.CreateFoodItemRequest;
import com.fitlat.backend.dto.food.FoodItemResponse;
import com.fitlat.backend.entity.FoodCategory;
import com.fitlat.backend.service.FoodItemService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foods")
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public List<FoodItemResponse> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) FoodCategory category
    ) {
        return foodItemService.search(keyword, category);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public FoodItemResponse getById(@PathVariable Long id) {
        return foodItemService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public FoodItemResponse create(
            @Valid @RequestBody CreateFoodItemRequest request
    ) {
        return foodItemService.create(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public FoodItemResponse update(
            @PathVariable Long id,
            @Valid @RequestBody CreateFoodItemRequest request
    ) {
        return foodItemService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public void deactivate(@PathVariable Long id) {
        foodItemService.deactivate(id);
    }
}