
package com.fitlat.backend.service;

import com.fitlat.backend.dto.exercise.*;
import com.fitlat.backend.entity.*;
import com.fitlat.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
public class ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final MuscleGroupRepository muscleGroupRepository;
    private final EquipmentRepository equipmentRepository;

    public ExerciseService(
        ExerciseRepository exerciseRepository,
        MuscleGroupRepository muscleGroupRepository,
        EquipmentRepository equipmentRepository
    ) {
        this.exerciseRepository = exerciseRepository;
        this.muscleGroupRepository = muscleGroupRepository;
        this.equipmentRepository = equipmentRepository;
    }

    // Lấy danh sách bài tập, hỗ trợ tìm kiếm và lọc
    public List<ExerciseResponse> search(
        String keyword,
        DifficultyLevel difficulty,
        Long muscleGroupId,
        Long equipmentId
    ) {
        String normalizedKeyword = (keyword == null || keyword.isBlank())
        ? null
        : keyword.trim().toLowerCase();

        return exerciseRepository.search(
            normalizedKeyword,
            normalizedKeyword != null,
            difficulty,
            muscleGroupId,
            equipmentId
        ).stream()
            .map(this::toResponse)
            .toList();
    }

    // Lấy chi tiết bài tập
    public ExerciseResponse getById(Long id) {
        Exercise exercise = findActiveExercise(id);
        return toResponse(exercise);
    }

    // Lấy danh sách nhóm cơ đang hoạt động
    public List<MuscleGroupResponse> getMuscleGroups() {
        return muscleGroupRepository.findAllByActiveTrueOrderByNameEnAsc()
            .stream()
            .map(group -> new MuscleGroupResponse(
                group.getId(),
                group.getCode(),
                group.getNameVi(),
                group.getNameEn()
            ))
            .toList();
    }

    // Lấy danh sách thiết bị đang hoạt động
    public List<EquipmentResponse> getEquipment() {
        return equipmentRepository.findAllByActiveTrueOrderByNameEnAsc()
            .stream()
            .map(item -> new EquipmentResponse(
                item.getId(),
                item.getCode(),
                item.getNameVi(),
                item.getNameEn()
            ))
            .toList();
    }

    // Tạo bài tập mới
    @Transactional
    public ExerciseResponse create(CreateExerciseRequest request) {
        if (exerciseRepository.existsByNameEnIgnoreCaseAndActiveTrue(
            request.nameEn().trim()
        )) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Exercise name already exists"
            );
        }

        validateAssociations(
            request.muscleGroups(),
            request.equipmentIds()
        );

        Exercise exercise = new Exercise(
            request.nameVi().trim(),
            request.nameEn().trim(),
            request.description(),
            request.instructions(),
            request.difficulty(),
            request.videoUrl()
        );

        // Lưu trước để Exercise có ID
        exercise = exerciseRepository.save(exercise);

        addAssociations(
            exercise,
            request.muscleGroups(),
            request.equipmentIds()
        );

        return toResponse(exerciseRepository.save(exercise));
    }

    // Cập nhật toàn bộ thông tin bài tập
    @Transactional
    public ExerciseResponse update(
        Long id,
        UpdateExerciseRequest request
    ) {
        Exercise exercise = findActiveExercise(id);

        if (exerciseRepository.existsByNameEnIgnoreCaseAndActiveTrueAndIdNot(
            request.nameEn().trim(),
            id
        )) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Exercise name already exists"
            );
        }

        validateAssociations(
            request.muscleGroups(),
            request.equipmentIds()
        );

        exercise.update(
            request.nameVi().trim(),
            request.nameEn().trim(),
            request.description(),
            request.instructions(),
            request.difficulty(),
            request.videoUrl()
        );

        // Xóa các liên kết cũ
    exercise.clearAssociations();

    // Buộc Hibernate thực hiện DELETE các liên kết cũ
    // trước khi thêm các liên kết mới
    exerciseRepository.flush();

    // Thêm các liên kết mới
    addAssociations(
        exercise,
        request.muscleGroups(),
        request.equipmentIds()
    );

        return toResponse(exerciseRepository.save(exercise));
    }

    // Ngừng hoạt động bài tập
    @Transactional
    public void deactivate(Long id) {
        Exercise exercise = findActiveExercise(id);
        exercise.deactivate();
        exerciseRepository.save(exercise);
    }

    // Tìm bài tập đang hoạt động
    private Exercise findActiveExercise(Long id) {
        return exerciseRepository.findByIdAndActiveTrue(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Exercise not found"
            ));
    }

    // Kiểm tra các ID và dữ liệu liên kết
    private void validateAssociations(
        List<ExerciseMuscleGroupRequest> muscleRequests,
        List<Long> equipmentIds
    ) {
        Set<Long> muscleIds = new HashSet<>();

        for (ExerciseMuscleGroupRequest item : muscleRequests) {
            if (!muscleIds.add(item.muscleGroupId())) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duplicate muscle group ID"
                );
            }

            if (muscleGroupRepository.findByIdAndActiveTrue(
                item.muscleGroupId()
            ).isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Muscle group not found: " + item.muscleGroupId()
                );
            }
        }

        Set<Long> uniqueEquipmentIds = new HashSet<>();

        for (Long equipmentId : equipmentIds) {
            if (!uniqueEquipmentIds.add(equipmentId)) {
                throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Duplicate equipment ID"
                );
            }

            if (equipmentRepository.findByIdAndActiveTrue(
                equipmentId
            ).isEmpty()) {
                throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Equipment not found: " + equipmentId
                );
            }
        }
    }

    // Tạo các liên kết bài tập - nhóm cơ - thiết bị
    private void addAssociations(
        Exercise exercise,
        List<ExerciseMuscleGroupRequest> muscleRequests,
        List<Long> equipmentIds
    ) {
        for (ExerciseMuscleGroupRequest item : muscleRequests) {
            MuscleGroup muscleGroup =
                muscleGroupRepository.findByIdAndActiveTrue(
                    item.muscleGroupId()
                ).orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Muscle group not found"
                ));

            exercise.addMuscleGroup(
                muscleGroup,
                item.muscleRole()
            );
        }

        for (Long equipmentId : equipmentIds) {
            Equipment equipment =
                equipmentRepository.findByIdAndActiveTrue(equipmentId)
                    .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Equipment not found"
                    ));

            exercise.addEquipment(equipment);
        }
    }

    // Chuyển Entity thành Response DTO
    private ExerciseResponse toResponse(Exercise exercise) {
        List<ExerciseMuscleGroupResponse> muscles =
            exercise.getMuscleGroups().stream()
                .map(item -> new ExerciseMuscleGroupResponse(
                    item.getMuscleGroup().getId(),
                    item.getMuscleGroup().getCode(),
                    item.getMuscleGroup().getNameVi(),
                    item.getMuscleGroup().getNameEn(),
                    item.getMuscleRole()
                ))
                .toList();

        List<EquipmentResponse> equipment =
            exercise.getEquipment().stream()
                .map(item -> new EquipmentResponse(
                    item.getEquipment().getId(),
                    item.getEquipment().getCode(),
                    item.getEquipment().getNameVi(),
                    item.getEquipment().getNameEn()
                ))
                .toList();

        return new ExerciseResponse(
            exercise.getId(),
            exercise.getNameVi(),
            exercise.getNameEn(),
            exercise.getDescription(),
            exercise.getInstructions(),
            exercise.getDifficulty(),
            exercise.getVideoUrl(),
            muscles,
            equipment,
            exercise.getCreatedAt(),
            exercise.getUpdatedAt()
        );
    }
}
