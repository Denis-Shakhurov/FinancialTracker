package org.example.app.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.app.application.service.TransactionService;
import org.example.app.domain.model.Category;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Контроллер для работы со статистикой по транзакциям.
 * Обрабатывает HTTP-запросы по пути "/api/transactions/statistics/*".
 * Предоставляет различные методы для получения статистических данных о транзакциях пользователя.
 */
@RestController
@RequestMapping("/api/transactions/statistics")
@RequiredArgsConstructor
@Tag(name = "Transaction Statistics", description = "Operations for transaction statistics")
public class StatisticController {
    private final TransactionService transactionService;

    @Operation(summary = "Get total consumption for user",
            description = "Returns the total consumption amount for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved consumption"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @GetMapping("/{userId}/consumption")
    public ResponseEntity<BigDecimal> getConsumption(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        BigDecimal consumption = transactionService.getConsumptionByUserId(userId);
        return ResponseEntity.ok(consumption);
    }

    @Operation(summary = "Get total income for user",
            description = "Returns the total income amount for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved income"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @GetMapping("/{userId}/income")
    public ResponseEntity<BigDecimal> getIncome(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        BigDecimal income = transactionService.getIncomeByUserId(userId);
        return ResponseEntity.ok(income);
    }

    @Operation(summary = "Get balance for user",
            description = "Returns the current balance for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved balance"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @GetMapping("/{userId}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        BigDecimal balance = transactionService.getBalanceByUserId(userId);
        return ResponseEntity.ok(balance);
    }

    @Operation(summary = "Get monthly consumption for user",
            description = "Returns the monthly consumption amount for a specific user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved monthly consumption"),
            @ApiResponse(responseCode = "400", description = "Invalid user ID")
    })
    @GetMapping("/{userId}/consumption-by-month")
    public ResponseEntity<BigDecimal> getConsumptionByMonth(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId) {
        BigDecimal consumption = transactionService.getConsumptionByUserIdByMonth(userId);
        return ResponseEntity.ok(consumption);
    }

    @Operation(summary = "Get consumption for period",
            description = "Returns the consumption amount for a specific user within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved consumption for period"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/{userId}/consumption-by-period")
    public ResponseEntity<BigDecimal> getConsumptionByPeriod(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal consumption = transactionService.getConsumptionByUserIdByPeriodDate(userId, startDate, endDate);
        return ResponseEntity.ok(consumption);
    }

    @Operation(summary = "Get income for period",
            description = "Returns the income amount for a specific user within a date range")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved income for period"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/{userId}/income-by-period")
    public ResponseEntity<BigDecimal> getIncomeByPeriod(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Start date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @Parameter(description = "End date (yyyy-MM-dd)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        BigDecimal income = transactionService.getIncomeByUserIdByPeriodDate(userId, startDate, endDate);
        return ResponseEntity.ok(income);
    }

    @Operation(summary = "Get consumption by category",
            description = "Returns the consumption amount for a specific user by category")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved consumption by category"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    @GetMapping("/{userId}/consumption-by-category")
    public ResponseEntity<BigDecimal> getConsumptionByCategory(
            @Parameter(description = "User ID", required = true)
            @PathVariable Long userId,
            @Parameter(description = "Transaction category", required = true)
            @RequestParam Category category) {
        BigDecimal consumption = transactionService.getConsumptionByUserIdByCategory(userId, category);
        return ResponseEntity.ok(consumption);
    }
}
