package com.fitlat.backend.controller;

import com.fitlat.backend.dto.meal.*;
import com.fitlat.backend.service.MealLogService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
public class MealLogController {

    private final MealLogService mealLogService;

    public MealLogController(MealLogService mealLogService) {
        this.mealLogService = mealLogService;
    }

    // 1. Tạo bữa ăn
    @PostMapping("/meals")
    @ResponseStatus(HttpStatus.CREATED)
    public MealLogResponse create(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateMealRequest request
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        return mealLogService.create(userId, request);
    }

    // 2. Lấy danh sách bữa ăn trong ngày
    @GetMapping("/meals")
    public List<MealLogResponse> getByDate(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        return mealLogService.getByDate(userId, date);
    }

    // 3. Xem chi tiết bữa ăn
    @GetMapping("/meals/{mealId}")
    public MealLogResponse getById(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        return mealLogService.getById(userId, mealId);
    }

    // 4. Cập nhật thông tin bữa ăn
    @PutMapping("/meals/{mealId}")
    public MealLogResponse update(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId,
            @Valid @RequestBody UpdateMealRequest request
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        return mealLogService.update(userId, mealId, request);
    }

    // 5. Xóa bữa ăn
    @DeleteMapping("/meals/{mealId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        mealLogService.delete(userId, mealId);
    }

    // 6. Thêm thực phẩm vào bữa ăn
    @PostMapping("/meals/{mealId}/items")
    public MealLogResponse addItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId,
            @Valid @RequestBody MealItemRequest request
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        return mealLogService.addItem(userId, mealId, request);
    }

    // 7. Cập nhật khối lượng thực phẩm
    @PutMapping("/meals/{mealId}/items/{itemId}")
    public MealLogResponse updateItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateMealItemRequest request
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        return mealLogService.updateItem(
                userId, mealId, itemId, request
        );
    }

    // 8. Xóa thực phẩm khỏi bữa ăn
    @DeleteMapping("/meals/{mealId}/items/{itemId}")
    public MealLogResponse deleteItem(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable Long mealId,
            @PathVariable Long itemId
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        return mealLogService.deleteItem(
                userId, mealId, itemId
        );
    }

    // 9. Thống kê dinh dưỡng trong ngày
    @GetMapping("/nutrition/daily")
    public DailyNutritionResponse getDailyNutrition(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        Long userId = Long.parseLong(jwt.getSubject());
        return mealLogService.getDailyNutrition(userId, date);
    }
}