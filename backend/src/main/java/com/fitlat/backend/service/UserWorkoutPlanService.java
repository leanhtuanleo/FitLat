
package com.fitlat.backend.service;

import com.fitlat.backend.dto.workout.*;
import com.fitlat.backend.entity.*;
import com.fitlat.backend.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserWorkoutPlanService {

    private final UserWorkoutPlanRepository planRepository;
    private final WorkoutProgramRepository programRepository;
    private final UserRepository userRepository;
    private final ExerciseRepository exerciseRepository;

    public UserWorkoutPlanService(
            UserWorkoutPlanRepository planRepository,
            WorkoutProgramRepository programRepository,
            UserRepository userRepository,
            ExerciseRepository exerciseRepository) {
        this.planRepository = planRepository;
        this.programRepository = programRepository;
        this.userRepository = userRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public List<UserWorkoutPlanResponse> getAll(Long userId) {
        return planRepository.findAllByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public UserWorkoutPlanResponse getById(Long userId, Long planId) {
        return toResponse(findUserPlan(userId, planId));
    }

    @Transactional
    public UserWorkoutPlanResponse create(
            Long userId, CreateWorkoutPlanRequest request) {

        validateDates(request.startDate(), request.endDate());

        User user = findUser(userId);

        UserWorkoutPlan plan = new UserWorkoutPlan(
                user,
                null,
                request.name(),
                request.durationWeeks(),
                request.daysPerWeek(),
                request.startDate(),
                request.endDate()
        );

        return toResponse(planRepository.save(plan));
    }

    @Transactional
    public UserWorkoutPlanResponse copyProgram(
            Long userId, Long programId,
            CopyWorkoutProgramRequest request) {

        User user = findUser(userId);

        WorkoutProgram program = programRepository.findById(programId)
                .filter(WorkoutProgram::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Workout program not found"));

        UserWorkoutPlan plan = new UserWorkoutPlan(
                user,
                program,
                request.name(),
                program.getDurationWeeks(),
                program.getDaysPerWeek(),
                request.startDate(),
                null
        );

        planRepository.save(plan);

        for (WorkoutProgramExercise source : program.getExercises()) {
            plan.addExercise(new UserWorkoutPlanExercise(
                    plan,
                    source.getExercise(),
                    source.getDayNumber(),
                    source.getOrderIndex(),
                    source.getTargetSets(),
                    source.getRepsMin(),
                    source.getRepsMax(),
                    source.getRestSeconds(),
                    source.getNotes()
            ));
        }

        return toResponse(planRepository.save(plan));
    }

    @Transactional
    public UserWorkoutPlanResponse update(
            Long userId, Long planId,
            UpdateWorkoutPlanRequest request) {

        UserWorkoutPlan plan = findUserPlan(userId, planId);

        validateDates(request.startDate(), request.endDate());

        plan.update(
                request.name(),
                request.durationWeeks(),
                request.daysPerWeek(),
                request.startDate(),
                request.endDate()
        );

        return toResponse(plan);
    }

    @Transactional
    public UserWorkoutPlanResponse updateStatus(
            Long userId, Long planId,
            UpdateWorkoutPlanStatusRequest request) {

        UserWorkoutPlan plan = findUserPlan(userId, planId);
        plan.updateStatus(request.status());

        return toResponse(plan);
    }

    @Transactional
    public UserWorkoutPlanResponse replaceExercises(
            Long userId, Long planId,
            List<WorkoutProgramExerciseRequest> requests) {

        UserWorkoutPlan plan = findUserPlan(userId, planId);

        if (requests == null || requests.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Exercise list must not be empty");
        }

        validateExercises(requests, plan.getDaysPerWeek());

        plan.clearExercises();
        planRepository.flush();

        Map<Long, Exercise> exerciseMap =
                exerciseRepository.findAllById(
                        requests.stream()
                                .map(WorkoutProgramExerciseRequest::exerciseId)
                                .distinct()
                                .toList()
                ).stream().collect(Collectors.toMap(
                        Exercise::getId, exercise -> exercise));

        for (WorkoutProgramExerciseRequest request : requests) {
            plan.addExercise(new UserWorkoutPlanExercise(
                    plan,
                    exerciseMap.get(request.exerciseId()),
                    request.dayNumber(),
                    request.orderIndex(),
                    request.targetSets(),
                    request.repsMin(),
                    request.repsMax(),
                    request.restSeconds(),
                    request.notes()
            ));
        }

        return toResponse(planRepository.save(plan));
    }

    @Transactional
    public void archive(Long userId, Long planId) {
        UserWorkoutPlan plan = findUserPlan(userId, planId);
        plan.updateStatus(WorkoutPlanStatus.ARCHIVED);
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found"));
    }

    private UserWorkoutPlan findUserPlan(Long userId, Long planId) {
        return planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Workout plan not found"));
    }

    private void validateDates(
            java.time.LocalDate startDate,
            java.time.LocalDate endDate) {

        if (startDate != null && endDate != null
                && endDate.isBefore(startDate)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "End date must not be before start date");
        }
    }

    private void validateExercises(
            List<WorkoutProgramExerciseRequest> requests,
            Integer daysPerWeek) {

        for (WorkoutProgramExerciseRequest request : requests) {
            if (request.dayNumber() > daysPerWeek
                    || request.repsMax() < request.repsMin()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid exercise schedule or repetition range");
            }
        }

        List<Long> ids = requests.stream()
                .map(WorkoutProgramExerciseRequest::exerciseId)
                .distinct()
                .toList();

        List<Exercise> exercises = exerciseRepository.findAllById(ids);

        if (exercises.size() != ids.size()
                || exercises.stream().anyMatch(e -> !e.isActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "One or more exercises are invalid or inactive");
        }
    }

    private UserWorkoutPlanResponse toResponse(UserWorkoutPlan plan) {
        List<WorkoutProgramExerciseResponse> exercises =
                plan.getExercises().stream()
                        .sorted(Comparator
                                .comparing(UserWorkoutPlanExercise::getDayNumber)
                                .thenComparing(UserWorkoutPlanExercise::getOrderIndex))
                        .map(exercise -> new WorkoutProgramExerciseResponse(
                                exercise.getId(),
                                exercise.getExercise().getId(),
                                exercise.getExercise().getNameVi(),
                                exercise.getExercise().getNameEn(),
                                exercise.getDayNumber(),
                                exercise.getOrderIndex(),
                                exercise.getTargetSets(),
                                exercise.getRepsMin(),
                                exercise.getRepsMax(),
                                exercise.getRestSeconds(),
                                exercise.getNotes()
                        ))
                        .toList();

        return new UserWorkoutPlanResponse(
                plan.getId(),
                plan.getSourceProgram() != null
                        ? plan.getSourceProgram().getId() : null,
                plan.getName(),
                plan.getDurationWeeks(),
                plan.getDaysPerWeek(),
                plan.getStatus(),
                plan.getStartDate(),
                plan.getEndDate(),
                exercises,
                plan.getCreatedAt(),
                plan.getUpdatedAt()
        );
    }
}
