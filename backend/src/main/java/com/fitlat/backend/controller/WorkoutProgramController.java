
package com.fitlat.backend.controller;

import com.fitlat.backend.dto.workout.*;
import com.fitlat.backend.service.WorkoutProgramService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workout-programs")
public class WorkoutProgramController {

    private final WorkoutProgramService programService;

    public WorkoutProgramController(WorkoutProgramService programService) {
        this.programService = programService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public List<WorkoutProgramResponse> getAll() {
        return programService.getAll();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('MEMBER', 'TRAINER', 'ADMIN')")
    public WorkoutProgramResponse getById(@PathVariable Long id) {
        return programService.getById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<WorkoutProgramResponse> create(
            @Valid @RequestBody CreateWorkoutProgramRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(programService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public WorkoutProgramResponse update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateWorkoutProgramRequest request) {
        return programService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        programService.deactivate(id);
        return ResponseEntity.noContent().build();
    }
}
