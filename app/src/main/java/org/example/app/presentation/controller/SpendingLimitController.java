package org.example.app.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.limit.SpendingLimitCreateDTO;
import org.example.app.application.dto.limit.SpendingLimitDTO;
import org.example.app.application.dto.limit.SpendingLimitEditDTO;
import org.example.app.application.service.SpendingLimitService;
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
 * Контроллер для работы с лимитами расходов.
 * Обрабатывает HTTP-запросы по пути "/api/limits/*" и выполняет CRUD-операции с лимитами.
 */
@Validated
@RestController
@RequestMapping("/api/limits")
@RequiredArgsConstructor
@Tag(name = "Spending Limit Management", description = "Operations pertaining to spending limits")
public class SpendingLimitController {
    private final SpendingLimitService spendingLimitService;

    @Operation(summary = "Get spending limit by ID", description = "Returns a single spending limit")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved limit"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Limit not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SpendingLimitDTO> show(
            @Parameter(description = "ID of limit to be retrieved", required = true)
            @PathVariable Long id) {
        SpendingLimitDTO limitDTO = spendingLimitService.getById(id);
        return ResponseEntity.ok(limitDTO);
    }

    @Operation(summary = "Get all spending limits by user ID",
            description = "Returns list of spending limits for a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved limits"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID supplied"),
            @ApiResponse(responseCode = "404", description = "No limits found for this user")
    })
    @GetMapping("/{userId}/user")
    public ResponseEntity<List<SpendingLimitDTO>> showAllByUser(
            @Parameter(description = "ID of user whose limits to be retrieved", required = true)
            @PathVariable Long userId) {
        List<SpendingLimitDTO> limits = spendingLimitService.findAllByUserId(userId);
        return ResponseEntity.ok(limits);
    }

    @Operation(summary = "Create a new spending limit", description = "Creates a new spending limit record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created limit"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Resource not found")
    })
    @PostMapping
    public ResponseEntity<String> create(
            @Parameter(description = "Spending limit object that needs to be created", required = true)
            @RequestBody @Valid SpendingLimitCreateDTO createDTO) {
        spendingLimitService.create(createDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Successfully created limit");
    }

    @Operation(summary = "Update an existing spending limit", description = "Updates spending limit data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated limit"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Limit not found")
    })
    @PostMapping("/{id}")
    public ResponseEntity<String> update(
            @Parameter(description = "ID of limit to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated limit data", required = true)
            @RequestBody @Valid SpendingLimitEditDTO editDTO) {
        editDTO.setId(id);
        spendingLimitService.update(editDTO);
        return ResponseEntity.ok("Successfully updated limit");
    }

    @Operation(summary = "Delete a spending limit", description = "Deletes a spending limit by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted limit"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Limit not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of limit to be deleted", required = true)
            @PathVariable Long id) {
        spendingLimitService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
