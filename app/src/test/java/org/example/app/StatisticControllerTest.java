package org.example.app;

import org.example.app.application.handler.GlobalExceptionHandler;
import org.example.app.application.service.TransactionService;
import org.example.app.domain.model.Category;
import org.example.app.presentation.controller.StatisticController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StatisticControllerTest {
    private static final String BASE_URL = "/api/transactions/statistics/1/";

    private MockMvc mockMvc;

    @Mock
    private TransactionService mockService;

    @InjectMocks
    private StatisticController controller;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /transactions/statistics/consumption returns correct consumption data for a user")
    public void handleConsumptionReturnsCorrectDataTest() throws Exception {
        BigDecimal expected = new BigDecimal("1500.00");
        when(mockService.getConsumptionByUserId(anyLong())).thenReturn(expected);

        mockMvc.perform(get(BASE_URL + "consumption"))
            .andExpect(status().isOk())
            .andExpect(content().string(expected.toString()));
    }

    @Test
    @DisplayName("/transactions/statistics/income returns correct income amount for a user")
    public void handleIncomeReturnsCorrectIncomeTest() throws Exception {
        BigDecimal expectedIncome = new BigDecimal("1000.00");
        when(mockService.getIncomeByUserId(anyLong())).thenReturn(expectedIncome);

        mockMvc.perform(get(BASE_URL + "income"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedIncome.toString()));
    }

    @Test
    @DisplayName("/transactions/statistics/balance returns correct balance calculation for a user")
    public void handleBalanceReturnsCorrectBalanceTest() throws Exception {
        BigDecimal expectedBalance = new BigDecimal("100.00");
        when(mockService.getBalanceByUserId(anyLong())).thenReturn(expectedBalance);

        mockMvc.perform(get(BASE_URL + "balance"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedBalance.toString()));
    }

    @Test
    @DisplayName("/transactions/statistics/consumption-by-month returns correct monthly consumption for a user")
    public void handleConsumptionByMonthReturnsCorrectValueTest() throws Exception {
        BigDecimal expectedConsumption = new BigDecimal("100.00");
        when(mockService.getConsumptionByUserIdByMonth(anyLong())).thenReturn(expectedConsumption);

        mockMvc.perform(get(BASE_URL + "consumption-by-month"))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedConsumption.toString()));
    }

    @Test
    @DisplayName("/transactions/statistics/consumption-by-period correctly processes date parameters and returns consumption for period")
    public void handleConsumptionByPeriodProcessesDatesCorrectlyTest() throws Exception {
        BigDecimal expectedConsumption = new BigDecimal("100.00");
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);

        when(mockService.getConsumptionByUserIdByPeriodDate(anyLong(),
                any(), any())).thenReturn(expectedConsumption);

        mockMvc.perform(get(BASE_URL + "consumption-by-period")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedConsumption.toString()));
    }

    @Test
    @DisplayName("/transactions/statistics/income-by-period correctly processes date parameters and returns income for period")
    public void handleIncomeByPeriodCorrectlyProcessesDatesAndReturnsIncomeTest() throws Exception {
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 1, 31);
        BigDecimal expectedIncome = new BigDecimal("1000.00");

        when(mockService.getIncomeByUserIdByPeriodDate(anyLong(), any(), any())).thenReturn(expectedIncome);

        mockMvc.perform(get(BASE_URL + "income-by-period")
                        .param("startDate", startDate.toString())
                        .param("endDate", endDate.toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedIncome.toString()));
    }

    @Test
    @DisplayName("/transactions/statistics/consumption-by-category correctly processes category parameter and returns consumption by category")
    public void handleConsumptionByCategoryTest() throws Exception {
        Category category = Category.PRODUCTS;
        BigDecimal expectedConsumption = new BigDecimal("100.00");

        when(mockService.getConsumptionByUserIdByCategory(anyLong(), any())).thenReturn(expectedConsumption);

        mockMvc.perform(get(BASE_URL + "consumption-by-category")
                        .param("category", category.name()))
                .andExpect(status().isOk())
                .andExpect(content().string(expectedConsumption.toString()));
    }
}
