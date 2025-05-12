package org.example.app.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.transaction.TransactionCreateDTO;
import org.example.app.application.dto.transaction.TransactionDTO;
import org.example.app.application.dto.transaction.TransactionEditDTO;
import org.example.app.application.service.TransactionService;
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
 * Контроллер для работы с транзакциями.
 * Обрабатывает HTTP-запросы по пути "/api/transactions/*" и выполняет CRUD-операции с транзакциями.
 */
@Validated
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Tag(name = "Transaction Management", description = "Operations pertaining to transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @Operation(summary = "Get all transactions",
            description = "Returns a list of all transactions",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of transactions")
            })
    @GetMapping
    public ResponseEntity<List<TransactionDTO>> getAll() {
        List<TransactionDTO> transactions = transactionService.getAll();
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Get transaction by ID",
            description = "Returns a single transaction by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transaction"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionDTO> show(
            @Parameter(description = "ID of transaction to be retrieved", required = true)
            @PathVariable Long id) {
        TransactionDTO transaction = transactionService.getById(id);
        return ResponseEntity.ok(transaction);
    }

    @Operation(summary = "Get all transactions by user ID",
            description = "Returns all transactions for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved transactions"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID supplied"),
            @ApiResponse(responseCode = "404", description = "No transactions found for this user")
    })
    @GetMapping("/{userId}/user/")
    public ResponseEntity<List<TransactionDTO>> showAllByUserId(
            @Parameter(description = "ID of user whose transactions to be retrieved", required = true)
            @PathVariable(value = "userId") Long userId) {
        List<TransactionDTO> transactions = transactionService.getAllByUserId(userId);
        return ResponseEntity.ok(transactions);
    }

    @Operation(summary = "Create a new transaction",
            description = "Creates a new transaction record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created transaction"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<String> create(
            @Parameter(description = "Transaction object that needs to be created", required = true)
            @RequestBody @Valid TransactionCreateDTO createDTO) {
        transactionService.create(createDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Transaction added");
    }

    @Operation(summary = "Update an existing transaction",
            description = "Updates transaction data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated transaction"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @PostMapping("/{id}")
    public ResponseEntity<String> update(
            @Parameter(description = "ID of transaction to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated transaction data", required = true)
            @RequestBody @Valid TransactionEditDTO editDTO) {
        editDTO.setId(id);
        transactionService.update(editDTO);
        return ResponseEntity.ok("Transaction updated successfully");
    }

    @Operation(summary = "Delete a transaction",
            description = "Deletes a transaction by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted transaction"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "Transaction not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of transaction to be deleted", required = true)
            @PathVariable Long id) {
        transactionService.delete(id);
        return ResponseEntity.notFound().build();
    }
}
