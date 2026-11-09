
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
public class WorkoutProgramService {

    private final WorkoutProgramRepository programRepository;
    private final ExerciseRepository exerciseRepository;

    public WorkoutProgramService(
            WorkoutProgramRepository programRepository,
            ExerciseRepository exerciseRepository) {
        this.programRepository = programRepository;
        this.exerciseRepository = exerciseRepository;
    }

    public List<WorkoutProgramResponse> getAll() {
        return programRepository.findAllByActiveTrueOrderByIdDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public WorkoutProgramResponse getById(Long id) {
        return toResponse(findActiveProgram(id));
    }

    @Transactional
    public WorkoutProgramResponse create(CreateWorkoutProgramRequest request) {
        validateProgramName(request.nameEn(), null);
        validateExercises(request.exercises(), request.daysPerWeek());

        WorkoutProgram program = new WorkoutProgram(
                request.nameVi(),
                request.nameEn(),
                request.description(),
                request.difficulty(),
                request.durationWeeks(),
                request.daysPerWeek()
        );

        programRepository.save(program);
        addExercises(program, request.exercises());

        return toResponse(programRepository.save(program));
    }

    @Transactional
    public WorkoutProgramResponse update(
            Long id, UpdateWorkoutProgramRequest request) {

        WorkoutProgram program = findActiveProgram(id);

        validateProgramName(request.nameEn(), id);
        validateExercises(request.exercises(), request.daysPerWeek());

        program.update(
                request.nameVi(),
                request.nameEn(),
                request.description(),
                request.difficulty(),
                request.durationWeeks(),
                request.daysPerWeek()
        );

        program.clearExercises();
        programRepository.flush();

        addExercises(program, request.exercises());

        return toResponse(programRepository.save(program));
    }

    @Transactional
    public void deactivate(Long id) {
        WorkoutProgram program = findActiveProgram(id);
        program.deactivate();
    }

    private WorkoutProgram findActiveProgram(Long id) {
        WorkoutProgram program = programRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Workout program not found"));

        if (!program.isActive()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Workout program not found");
        }

        return program;
    }

    private void validateProgramName(String nameEn, Long currentId) {
        boolean exists = currentId == null
                ? programRepository.existsByNameEnIgnoreCaseAndActiveTrue(nameEn)
                : programRepository.existsByNameEnIgnoreCaseAndActiveTrueAndIdNot(
                        nameEn, currentId);

        if (exists) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Workout program name already exists");
        }
    }

    private void validateExercises(
            List<WorkoutProgramExerciseRequest> requests,
            Integer daysPerWeek) {

        Set<Long> ids = new HashSet<>();

        for (WorkoutProgramExerciseRequest request : requests) {
            if (request.dayNumber() > daysPerWeek) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Exercise day number exceeds days per week");
            }

            if (request.repsMax() < request.repsMin()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Maximum reps must be greater than or equal to minimum reps");
            }

            if (!ids.add(request.exerciseId())) {
                // Một bài tập có thể xuất hiện ở nhiều ngày,
                // nên không cấm trùng exerciseId.
                ids.remove(request.exerciseId());
            }
        }

        List<Long> exerciseIds = requests.stream()
                .map(WorkoutProgramExerciseRequest::exerciseId)
                .distinct()
                .toList();

        List<Exercise> exercises =
                exerciseRepository.findAllById(exerciseIds);

        if (exercises.size() != exerciseIds.size()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "One or more exercises do not exist");
        }

        if (exercises.stream().anyMatch(exercise -> !exercise.isActive())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Inactive exercises cannot be used");
        }
    }

    private void addExercises(
            WorkoutProgram program,
            List<WorkoutProgramExerciseRequest> requests) {

        Map<Long, Exercise> exerciseMap =
                exerciseRepository.findAllById(
                        requests.stream()
                                .map(WorkoutProgramExerciseRequest::exerciseId)
                                .distinct()
                                .toList()
                ).stream().collect(Collectors.toMap(
                        Exercise::getId, exercise -> exercise));

        for (WorkoutProgramExerciseRequest request : requests) {
            program.addExercise(new WorkoutProgramExercise(
                    program,
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
    }

    private WorkoutProgramResponse toResponse(WorkoutProgram program) {
        List<WorkoutProgramExerciseResponse> exercises =
                program.getExercises().stream()
                        .sorted(Comparator
                                .comparing(WorkoutProgramExercise::getDayNumber)
                                .thenComparing(WorkoutProgramExercise::getOrderIndex))
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

        return new WorkoutProgramResponse(
                program.getId(),
                program.getNameVi(),
                program.getNameEn(),
                program.getDescription(),
                program.getDifficulty(),
                program.getDurationWeeks(),
                program.getDaysPerWeek(),
                exercises,
                program.getCreatedAt(),
                program.getUpdatedAt()
        );
    }
}
