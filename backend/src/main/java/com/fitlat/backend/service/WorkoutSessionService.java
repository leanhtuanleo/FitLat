package com.fitlat.backend.service;

import com.fitlat.backend.dto.workout.logging.*;
import com.fitlat.backend.entity.*;
import com.fitlat.backend.repository.*;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class WorkoutSessionService {

    private final WorkoutSessionRepository sessionRepository;
    private final WorkoutSessionExerciseRepository sessionExerciseRepository;
    private final WorkoutSetLogRepository setLogRepository;
    private final UserRepository userRepository;
    private final UserWorkoutPlanRepository planRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutSessionService(
            WorkoutSessionRepository sessionRepository,
            WorkoutSessionExerciseRepository sessionExerciseRepository,
            WorkoutSetLogRepository setLogRepository,
            UserRepository userRepository,
            UserWorkoutPlanRepository planRepository,
            ExerciseRepository exerciseRepository) {

        this.sessionRepository = sessionRepository;
        this.sessionExerciseRepository = sessionExerciseRepository;
        this.setLogRepository = setLogRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
        this.exerciseRepository = exerciseRepository;
    }

    // 1. Get all workout sessions of current user
    public List<WorkoutSessionResponse> getAll(Long userId) {
        return sessionRepository
                .findAllByUserIdOrderBySessionDateDescStartedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 2. Get workout session detail
    public WorkoutSessionResponse getById(Long userId, Long sessionId) {
        return toResponse(findUserSession(userId, sessionId));
    }

    // 3. Create workout session
    @Transactional
    public WorkoutSessionResponse create(
            Long userId,
            CreateWorkoutSessionRequest request) {

        User user = findUser(userId);

        UserWorkoutPlan plan = null;

        if (request.workoutPlanId() != null) {
            plan = findUserPlan(userId, request.workoutPlanId());

            if (plan.getStatus() == WorkoutPlanStatus.ARCHIVED) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Cannot create a session from an archived plan");
            }
        }

        LocalDate sessionDate = request.sessionDate() != null
                ? request.sessionDate()
                : LocalDate.now();

        WorkoutSession session = new WorkoutSession(
                user,
                plan,
                sessionDate,
                request.notes()
        );

        return toResponse(sessionRepository.save(session));
    }

    // 4. Add exercise to a workout session
    @Transactional
    public WorkoutSessionResponse addExercise(
            Long userId,
            Long sessionId,
            AddSessionExerciseRequest request) {

        WorkoutSession session = findUserSession(userId, sessionId);
        ensureInProgress(session);

        if (request.targetRepsMin() != null
                && request.targetRepsMax() != null
                && request.targetRepsMax() < request.targetRepsMin()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Maximum reps must not be less than minimum reps");
        }

        boolean duplicateOrder = session.getExercises().stream()
                .anyMatch(e -> e.getOrderIndex().equals(request.orderIndex()));

        if (duplicateOrder) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Exercise order already exists in this session");
        }

        Exercise exercise = exerciseRepository.findById(request.exerciseId())
                .filter(Exercise::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Exercise not found or inactive"));

        UserWorkoutPlanExercise planExercise = null;

        if (request.planExerciseId() != null) {
            if (session.getWorkoutPlan() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "This session is not linked to a workout plan");
            }

            planExercise = session.getWorkoutPlan().getExercises()
                    .stream()
                    .filter(e -> e.getId().equals(request.planExerciseId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "Plan exercise does not belong to this workout plan"));

            if (!planExercise.getExercise().getId().equals(exercise.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Exercise does not match the selected plan exercise");
            }
        }

        WorkoutSessionExercise sessionExercise =
                new WorkoutSessionExercise(
                        session,
                        exercise,
                        planExercise,
                        request.orderIndex(),
                        request.targetSets(),
                        request.targetRepsMin(),
                        request.targetRepsMax(),
                        request.restSeconds(),
                        request.notes()
                );

        session.addExercise(sessionExercise);

        return toResponse(sessionRepository.save(session));
    }

    // 5. Add a set log
    @Transactional
    public WorkoutSessionResponse addSetLog(
            Long userId,
            Long sessionId,
            Long sessionExerciseId,
            CreateWorkoutSetLogRequest request) {

        WorkoutSession session = findUserSession(userId, sessionId);
        ensureInProgress(session);

        WorkoutSessionExercise sessionExercise =
                findSessionExercise(sessionId, sessionExerciseId);

        validateSetMetrics(
                request.reps(),
                request.durationSeconds(),
                request.distanceMeters()
        );

        boolean duplicateSetNumber = sessionExercise.getSetLogs().stream()
                .anyMatch(log -> log.getSetNumber().equals(request.setNumber()));

        if (duplicateSetNumber) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Set number already exists");
        }

        WorkoutSetLog log = new WorkoutSetLog(
                sessionExercise,
                request.setNumber(),
                request.reps(),
                request.weightKg(),
                request.durationSeconds(),
                request.distanceMeters(),
                request.rpe(),
                request.notes()
        );

        sessionExercise.addSetLog(log);

        return toResponse(sessionRepository.save(session));
    }

    // 6. Update a set log
    @Transactional
    public WorkoutSessionResponse updateSetLog(
            Long userId,
            Long sessionId,
            Long sessionExerciseId,
            Long setLogId,
            UpdateWorkoutSetLogRequest request) {

        WorkoutSession session = findUserSession(userId, sessionId);
        ensureInProgress(session);

        findSessionExercise(sessionId, sessionExerciseId);

        WorkoutSetLog log = findSetLog(sessionExerciseId, setLogId);

        validateSetMetrics(
                request.reps(),
                request.durationSeconds(),
                request.distanceMeters()
        );

        log.update(
                request.reps(),
                request.weightKg(),
                request.durationSeconds(),
                request.distanceMeters(),
                request.rpe(),
                request.notes()
        );

        return toResponse(session);
    }

    // 7. Delete a set log
    @Transactional
    public void deleteSetLog(
            Long userId,
            Long sessionId,
            Long sessionExerciseId,
            Long setLogId) {

        WorkoutSession session = findUserSession(userId, sessionId);
        ensureInProgress(session);

        findSessionExercise(sessionId, sessionExerciseId);

        WorkoutSetLog log = findSetLog(sessionExerciseId, setLogId);

        setLogRepository.delete(log);
    }

    // 8. Complete workout session
    @Transactional
    public WorkoutSessionResponse complete(
            Long userId,
            Long sessionId,
            CompleteWorkoutSessionRequest request) {

        WorkoutSession session = findUserSession(userId, sessionId);
        ensureInProgress(session);

        int durationMinutes;

        if (request.durationMinutes() != null) {
            durationMinutes = request.durationMinutes();
        } else {
            durationMinutes = Math.toIntExact(
                    Math.max(0,
                            Duration.between(
                                    session.getStartedAt(),
                                    LocalDateTime.now()
                            ).toMinutes())
            );
        }

        if (request.notes() != null) {
            session.updateNotes(request.notes());
        }

        session.complete(durationMinutes);

        return toResponse(session);
    }

    // 9. Cancel workout session
    @Transactional
    public void cancel(Long userId, Long sessionId) {
        WorkoutSession session = findUserSession(userId, sessionId);
        ensureInProgress(session);
        session.cancel();
    }

    // 10. Get progress of an exercise
    public WorkoutProgressResponse getExerciseProgress(
            Long userId,
            Long exerciseId) {

        Exercise exercise = exerciseRepository.findById(exerciseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exercise not found"));

        List<WorkoutSession> sessions = sessionRepository
                .findAllByUserIdOrderBySessionDateDescStartedAtDesc(userId);

        List<WorkoutProgressResponse.ProgressPoint> history =
                new ArrayList<>();

        long totalSets = 0;
        BigDecimal bestWeight = null;
        Integer bestReps = null;
        LocalDate latestSessionDate = null;
        List<Long> completedSessionIds = new ArrayList<>();

        for (WorkoutSession session : sessions) {

            if (session.getStatus() != WorkoutSessionStatus.COMPLETED) {
                continue;
            }

            for (WorkoutSessionExercise sessionExercise
                    : session.getExercises()) {

                if (!sessionExercise.getExercise().getId().equals(exerciseId)) {
                    continue;
                }

                completedSessionIds.add(session.getId());

                if (latestSessionDate == null
                        || session.getSessionDate().isAfter(latestSessionDate)) {
                    latestSessionDate = session.getSessionDate();
                }

                for (WorkoutSetLog log : sessionExercise.getSetLogs()) {
                    totalSets++;

                    if (log.getWeightKg() != null
                            && (bestWeight == null
                            || log.getWeightKg().compareTo(bestWeight) > 0)) {
                        bestWeight = log.getWeightKg();
                    }

                    if (log.getReps() != null
                            && (bestReps == null
                            || log.getReps() > bestReps)) {
                        bestReps = log.getReps();
                    }

                    history.add(
                            new WorkoutProgressResponse.ProgressPoint(
                                    session.getId(),
                                    session.getSessionDate(),
                                    log.getSetNumber(),
                                    log.getReps(),
                                    log.getWeightKg(),
                                    log.getRpe()
                            )
                    );
                }
            }
        }

        history.sort(
                Comparator.comparing(
                        WorkoutProgressResponse.ProgressPoint::sessionDate
                ).thenComparing(
                        WorkoutProgressResponse.ProgressPoint::setNumber
                )
        );

        return new WorkoutProgressResponse(
                exercise.getId(),
                exercise.getNameVi(),
                exercise.getNameEn(),
                completedSessionIds.stream().distinct().count(),
                totalSets,
                bestWeight,
                bestReps,
                latestSessionDate,
                history
        );
    }

    // -------------------- Helper methods --------------------

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"));
    }

    private UserWorkoutPlan findUserPlan(Long userId, Long planId) {
        return planRepository.findByIdAndUserId(planId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Workout plan not found"));
    }

    private WorkoutSession findUserSession(Long userId, Long sessionId) {
        return sessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Workout session not found"));
    }

    private WorkoutSessionExercise findSessionExercise(
            Long sessionId,
            Long sessionExerciseId) {

        return sessionExerciseRepository
                .findByIdAndSessionId(sessionExerciseId, sessionId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Exercise not found in this workout session"));
    }

    private WorkoutSetLog findSetLog(
            Long sessionExerciseId,
            Long setLogId) {

        return setLogRepository
                .findByIdAndSessionExerciseId(setLogId, sessionExerciseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Set log not found"));
    }

    private void ensureInProgress(WorkoutSession session) {
        if (session.getStatus() != WorkoutSessionStatus.IN_PROGRESS) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Workout session is not in progress");
        }
    }

    private void validateSetMetrics(
            Integer reps,
            Integer durationSeconds,
            BigDecimal distanceMeters) {

        if (reps == null
                && durationSeconds == null
                && distanceMeters == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "At least one performance metric is required");
        }
    }

    // -------------------- Response mapping --------------------

    private WorkoutSessionResponse toResponse(WorkoutSession session) {

        List<WorkoutSessionExerciseResponse> exercises =
                session.getExercises().stream()
                        .sorted(Comparator.comparing(
                                WorkoutSessionExercise::getOrderIndex))
                        .map(exercise ->
                                new WorkoutSessionExerciseResponse(
                                        exercise.getId(),
                                        exercise.getExercise().getId(),
                                        exercise.getPlanExercise() != null
                                                ? exercise.getPlanExercise().getId()
                                                : null,
                                        exercise.getExerciseNameVi(),
                                        exercise.getExerciseNameEn(),
                                        exercise.getOrderIndex(),
                                        exercise.getTargetSets(),
                                        exercise.getTargetRepsMin(),
                                        exercise.getTargetRepsMax(),
                                        exercise.getRestSeconds(),
                                        exercise.getNotes(),
                                        exercise.getSetLogs().stream()
                                                .sorted(Comparator.comparing(
                                                        WorkoutSetLog::getSetNumber))
                                                .map(this::toSetLogResponse)
                                                .toList()
                                )
                        )
                        .toList();

        return new WorkoutSessionResponse(
                session.getId(),
                session.getWorkoutPlan() != null
                        ? session.getWorkoutPlan().getId()
                        : null,
                session.getSessionDate(),
                session.getStatus(),
                session.getStartedAt(),
                session.getCompletedAt(),
                session.getDurationMinutes(),
                session.getNotes(),
                exercises
        );
    }

    private WorkoutSetLogResponse toSetLogResponse(WorkoutSetLog log) {
        return new WorkoutSetLogResponse(
                log.getId(),
                log.getSetNumber(),
                log.getReps(),
                log.getWeightKg(),
                log.getDurationSeconds(),
                log.getDistanceMeters(),
                log.getRpe(),
                log.getNotes(),
                log.getCreatedAt()
        );
    }
}