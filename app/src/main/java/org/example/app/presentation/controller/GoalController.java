package org.example.app.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.goal.GoalCreateDTO;
import org.example.app.application.dto.goal.GoalDTO;
import org.example.app.application.dto.goal.GoalEditDTO;
import org.example.app.application.service.GoalService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Контроллер для работы с целями.
 * Обрабатывает HTTP-запросы по пути "/api/goals/*" и выполняет CRUD-операции с целями.
 */
@Validated
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Tag(name = "Goal Management", description = "Operations pertaining to goals")
public class GoalController {
    private final GoalService goalService;

    @Operation(summary = "Get goal by ID", description = "Returns a single goal")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved goal"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<GoalDTO> show(
            @Parameter(description = "ID of goal to be retrieved", required = true)
            @PathVariable Long id) {
        GoalDTO goalDTO = goalService.getById(id);
        return ResponseEntity.ok(goalDTO);
    }

    @Operation(summary = "Get all goals by user ID", description = "Returns list of goals for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved goals"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID supplied"),
            @ApiResponse(responseCode = "404", description = "No goals found for this user")
    })
    @GetMapping("/{userId}/user")
    public ResponseEntity<List<GoalDTO>> showAllByUser(
            @Parameter(description = "ID of user whose goals to be retrieved", required = true)
            @PathVariable Long userId) {
        List<GoalDTO> goals = goalService.getAllByUser(userId);
        return ResponseEntity.ok(goals);
    }

    @Operation(summary = "Create a new goal", description = "Creates a new goal record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created goal"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<String> create(
            @Parameter(description = "Goal object that needs to be created", required = true)
            @RequestBody @Valid GoalCreateDTO goalCreateDTO) {
        goalService.create(goalCreateDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Successfully created goal");
    }

    @Operation(summary = "Update an existing goal", description = "Updates goal data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated goal"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @PostMapping("/{id}")
    public ResponseEntity<String> update(
            @Parameter(description = "ID of goal to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated goal data", required = true)
            @RequestBody @Valid GoalEditDTO goalEditDTO) {
        goalEditDTO.setId(id);
        goalService.update(goalEditDTO);
        return ResponseEntity.ok("Successfully updated goal");
    }

    @Operation(summary = "Delete a goal", description = "Deletes a goal by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted goal"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Goal not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of goal to be deleted", required = true)
            @PathVariable Long id) {
        goalService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
