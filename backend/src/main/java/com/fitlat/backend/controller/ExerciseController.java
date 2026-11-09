
package com.fitlat.backend.controller;

import com.fitlat.backend.dto.exercise.*;
import com.fitlat.backend.entity.DifficultyLevel;
import com.fitlat.backend.service.ExerciseService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExerciseController {

    private final ExerciseService exerciseService;

    public ExerciseController(ExerciseService exerciseService) {
        this.exerciseService = exerciseService;
    }

    // Danh sách và tìm kiếm bài tập
    @GetMapping("/exercises")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public List<ExerciseResponse> search(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) DifficultyLevel difficulty,
        @RequestParam(required = false) Long muscleGroupId,
        @RequestParam(required = false) Long equipmentId
    ) {
        return exerciseService.search(
            keyword, difficulty, muscleGroupId, equipmentId
        );
    }

    // Chi tiết bài tập
    @GetMapping("/exercises/{id}")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public ExerciseResponse getById(@PathVariable Long id) {
        return exerciseService.getById(id);
    }

    // Danh sách nhóm cơ
    @GetMapping("/muscle-groups")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public List<MuscleGroupResponse> getMuscleGroups() {
        return exerciseService.getMuscleGroups();
    }

    // Danh sách thiết bị
    @GetMapping("/equipment")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public List<EquipmentResponse> getEquipment() {
        return exerciseService.getEquipment();
    }

    // ADMIN tạo bài tập
    @PostMapping("/exercises")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciseResponse create(
        @Valid @RequestBody CreateExerciseRequest request
    ) {
        return exerciseService.create(request);
    }

    // ADMIN cập nhật bài tập
    @PutMapping("/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ExerciseResponse update(
        @PathVariable Long id,
        @Valid @RequestBody UpdateExerciseRequest request
    ) {
        return exerciseService.update(id, request);
    }

    // ADMIN ngừng hoạt động bài tập
    @DeleteMapping("/exercises/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        exerciseService.deactivate(id);
    }
}
