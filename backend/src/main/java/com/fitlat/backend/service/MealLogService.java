package com.fitlat.backend.service;

import com.fitlat.backend.dto.meal.*;
import com.fitlat.backend.entity.*;
import com.fitlat.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class MealLogService {

    private static final BigDecimal ONE_HUNDRED =
            new BigDecimal("100");

    private static final BigDecimal ZERO =
            BigDecimal.ZERO.setScale(2);

    private final MealLogRepository mealLogRepository;
    private final MealLogItemRepository mealLogItemRepository;
    private final FoodItemRepository foodItemRepository;
    private final UserRepository userRepository;

    public MealLogService(
            MealLogRepository mealLogRepository,
            MealLogItemRepository mealLogItemRepository,
            FoodItemRepository foodItemRepository,
            UserRepository userRepository
    ) {
        this.mealLogRepository = mealLogRepository;
        this.mealLogItemRepository = mealLogItemRepository;
        this.foodItemRepository = foodItemRepository;
        this.userRepository = userRepository;
    }

    // 1. Tạo bữa ăn cùng danh sách thực phẩm
    @Transactional
    public MealLogResponse create(
            Long userId,
            CreateMealRequest request
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"
                ));

        MealLog meal = new MealLog(
                user,
                request.mealType(),
                request.consumedAt(),
                request.note()
        );

        mealLogRepository.save(meal);

        for (MealItemRequest itemRequest : request.items()) {
            FoodItem food = findActiveFood(itemRequest.foodItemId());

            MealLogItem item = new MealLogItem(
                    meal,
                    food,
                    itemRequest.quantityGrams()
            );

            mealLogItemRepository.save(item);
        }

        return toResponse(meal);
    }

    // 2. Lấy danh sách bữa ăn trong một ngày
    public List<MealLogResponse> getByDate(
            Long userId,
            LocalDate date
    ) {
        return mealLogRepository
                .findAllByUserIdAndConsumedAtGreaterThanEqualAndConsumedAtLessThanOrderByConsumedAtAsc(
                        userId,
                        date.atStartOfDay(),
                        date.plusDays(1).atStartOfDay()
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 3. Xem chi tiết một bữa ăn
    public MealLogResponse getById(Long userId, Long mealId) {
        return toResponse(findMeal(mealId, userId));
    }

    // 4. Cập nhật thông tin bữa ăn
    @Transactional
    public MealLogResponse update(
            Long userId,
            Long mealId,
            UpdateMealRequest request
    ) {
        MealLog meal = findMeal(mealId, userId);

        meal.update(
                request.mealType(),
                request.consumedAt(),
                request.note()
        );

        return toResponse(meal);
    }

    // 5. Xóa toàn bộ bữa ăn
    @Transactional
    public void delete(Long userId, Long mealId) {
        MealLog meal = findMeal(mealId, userId);
        mealLogRepository.delete(meal);
    }

    // 6. Thêm thực phẩm vào bữa ăn
    @Transactional
    public MealLogResponse addItem(
            Long userId,
            Long mealId,
            MealItemRequest request
    ) {
        MealLog meal = findMeal(mealId, userId);
        FoodItem food = findActiveFood(request.foodItemId());

        MealLogItem item = new MealLogItem(
                meal,
                food,
                request.quantityGrams()
        );

        mealLogItemRepository.save(item);

        return toResponse(meal);
    }

    // 7. Cập nhật khối lượng thực phẩm
    @Transactional
    public MealLogResponse updateItem(
            Long userId,
            Long mealId,
            Long itemId,
            UpdateMealItemRequest request
    ) {
        MealLog meal = findMeal(mealId, userId);
        MealLogItem item = findMealItem(itemId, mealId);

        item.updateQuantity(request.quantityGrams());

        return toResponse(meal);
    }

    // 8. Xóa thực phẩm khỏi bữa ăn
    @Transactional
    public MealLogResponse deleteItem(
            Long userId,
            Long mealId,
            Long itemId
    ) {
        MealLog meal = findMeal(mealId, userId);
        MealLogItem item = findMealItem(itemId, mealId);

        mealLogItemRepository.delete(item);

        return toResponse(meal);
    }

    // 9. Thống kê dinh dưỡng trong ngày
    public DailyNutritionResponse getDailyNutrition(
            Long userId,
            LocalDate date
    ) {
        List<MealLogResponse> meals = getByDate(userId, date);

        BigDecimal calories = ZERO;
        BigDecimal protein = ZERO;
        BigDecimal carbs = ZERO;
        BigDecimal fat = ZERO;
        BigDecimal fiber = ZERO;

        for (MealLogResponse meal : meals) {
            calories = calories.add(meal.totalCalories());
            protein = protein.add(meal.totalProtein());
            carbs = carbs.add(meal.totalCarbs());
            fat = fat.add(meal.totalFat());
            fiber = fiber.add(meal.totalFiber());
        }

        return new DailyNutritionResponse(
                date,
                meals.size(),
                calories,
                protein,
                carbs,
                fat,
                fiber
        );
    }

    // Tìm bữa ăn và kiểm tra quyền sở hữu
    private MealLog findMeal(Long mealId, Long userId) {
        return mealLogRepository.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Meal not found"
                ));
    }

    // Tìm thực phẩm đang hoạt động
    private FoodItem findActiveFood(Long foodId) {
        return foodItemRepository.findByIdAndActiveTrue(foodId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Food item not found"
                ));
    }

    // Tìm thực phẩm thuộc đúng bữa ăn
    private MealLogItem findMealItem(Long itemId, Long mealId) {
        return mealLogItemRepository.findByIdAndMealLogId(
                itemId, mealId
        ).orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Meal item not found"
        ));
    }

    // Tính dinh dưỡng thực tế theo khối lượng ăn
    private BigDecimal calculateNutrition(
            BigDecimal nutritionPer100g,
            BigDecimal quantityGrams
    ) {
        if (nutritionPer100g == null) {
            return ZERO;
        }

        return nutritionPer100g
                .multiply(quantityGrams)
                .divide(ONE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

    // Chuyển Entity sang Response DTO
    private MealLogResponse toResponse(MealLog meal) {

        List<MealLogItemResponse> items =
                mealLogItemRepository
                        .findAllByMealLogIdOrderByIdAsc(meal.getId())
                        .stream()
                        .map(this::toItemResponse)
                        .toList();

        BigDecimal totalCalories = ZERO;
        BigDecimal totalProtein = ZERO;
        BigDecimal totalCarbs = ZERO;
        BigDecimal totalFat = ZERO;
        BigDecimal totalFiber = ZERO;

        for (MealLogItemResponse item : items) {
            totalCalories = totalCalories.add(item.calories());
            totalProtein = totalProtein.add(item.protein());
            totalCarbs = totalCarbs.add(item.carbs());
            totalFat = totalFat.add(item.fat());
            totalFiber = totalFiber.add(item.fiber());
        }

        return new MealLogResponse(
                meal.getId(),
                meal.getMealType(),
                meal.getConsumedAt(),
                meal.getNote(),
                items,
                totalCalories,
                totalProtein,
                totalCarbs,
                totalFat,
                totalFiber
        );
    }

    // Chuyển một thực phẩm thành dữ liệu dinh dưỡng thực tế
    private MealLogItemResponse toItemResponse(MealLogItem item) {

        BigDecimal quantity = item.getQuantityGrams();

        return new MealLogItemResponse(
                item.getId(),
                item.getFoodItem().getId(),
                item.getFoodNameSnapshot(),
                quantity,
                calculateNutrition(item.getCaloriesPer100g(), quantity),
                calculateNutrition(item.getProteinPer100g(), quantity),
                calculateNutrition(item.getCarbsPer100g(), quantity),
                calculateNutrition(item.getFatPer100g(), quantity),
                calculateNutrition(item.getFiberPer100g(), quantity)
        );
    }
}