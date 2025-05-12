package org.example.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.application.dto.transaction.TransactionCreateDTO;
import org.example.app.application.dto.transaction.TransactionDTO;
import org.example.app.application.dto.transaction.TransactionEditDTO;
import org.example.app.application.exception.ResourceNotFoundException;
import org.example.app.application.handler.GlobalExceptionHandler;
import org.example.app.application.service.TransactionService;
import org.example.app.presentation.controller.TransactionController;
import org.example.app.util.TransactionGenerator;
import org.example.app.utils.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TransactionControllerTest {
    private static final String BASE_URL = "/api/transactions";
    private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;

    private MockMvc mockMvc;

    @Mock
    private TransactionService mockService;

    @InjectMocks
    private TransactionController controller;

    private final ObjectMapper mapper = new JsonUtil().getObjectMapper();
    private final TransactionGenerator generator = new TransactionGenerator();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/transactions/ returns all transactions returns a list of TransactionDTO objects")
    public void getAllTransactionsTest() throws Exception {
        TransactionDTO transactionDTO = generator.getTransactionDTO();
        TransactionDTO transactionDTO2 = generator.getTransactionDTO();

        List<TransactionDTO> expectedTransactions = Arrays.asList(
                transactionDTO, transactionDTO2
        );

        when(mockService.getAll()).thenReturn(expectedTransactions);

        mockMvc.perform(get(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description", containsString(transactionDTO.getDescription())))
                .andExpect(jsonPath("$[1].amount").value(transactionDTO2.getAmount()));
    }

    @Test
    @DisplayName("GET /api/transactions/{id} returns a transaction by ID returns the correct TransactionDTO")
    public void getTransactionByIdTest() throws Exception {
        TransactionDTO expectedTransaction = generator.getTransactionDTO();

        when(mockService.getById(1L)).thenReturn(expectedTransaction);

        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description",
                        containsString(expectedTransaction.getDescription())));
    }

    @Test
    @DisplayName("GET /api/transactions/{id}/user returns transactions by user ID returns the correct list of TransactionDTOs")
    public void getTransactionsByUserIdTest() throws Exception {
        TransactionDTO transactionDTO = generator.getTransactionDTO();
        TransactionDTO transactionDTO2 = generator.getTransactionDTO();

        List<TransactionDTO> expectedTransactions = Arrays.asList(
                transactionDTO, transactionDTO2
        );

        when(mockService.getAllByUserId(1L)).thenReturn(expectedTransactions);

        mockMvc.perform(get(BASE_URL + "/{userId}/user/", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description",
                        containsString(transactionDTO.getDescription())))
                .andExpect(jsonPath("$[1].description",
                        containsString(transactionDTO2.getDescription())));
    }

    @Test
    @DisplayName("POST /api/transactions creating a new transaction with valid data returns 201 CREATED status")
    public void createTransactionWithValidDataTest() throws Exception {
        TransactionCreateDTO createDTO = generator.getCreateDTO();

        when(mockService.create(any(TransactionCreateDTO.class))).thenReturn(1L);

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(mapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", containsString("Transaction added")));
    }

    @Test
    @DisplayName("POST /api/transactions/{id} updating an existing transaction with valid data returns 200 OK status")
    public void updateTransactionWithValidDataTest() throws Exception {
        TransactionEditDTO editDTO = generator.getEditDTO();

        doNothing().when(mockService).update(any(TransactionEditDTO.class));

        mockMvc.perform(post(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(mapper.writeValueAsString(editDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsString("Transaction updated successfully")));
    }

    @Test
    @DisplayName("DELETE /api/transactions/{id} deleting a transaction by ID returns 404 Not Found status")
    public void deleteTransactionByIdTest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /api/transactions/{id} show user with invalid id return 404 NOT FOUND")
    public void getTransactionByIdInvalidIdTest() throws Exception {
        when(mockService.getById(999L)).thenThrow(new ResourceNotFoundException("Transaction not found"));

        mockMvc.perform(get(BASE_URL + "/999")
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/transactions creating a transaction with invalid data should return 400 BAD REQUEST")
    public void createTransactionWithInvalidDataTest() throws Exception {
        TransactionCreateDTO createDTO = generator.getCreateDTO();

        when(mockService.create(any(TransactionCreateDTO.class))).
                thenThrow(IllegalArgumentException.class);

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(mapper.writeValueAsString(createDTO)))
               .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/transactions/{id} update transaction with invalid data return 400 BAD REQUEST")
    public void updateTransactionWithInvalidDataTest() throws Exception {
        TransactionEditDTO editDTO = generator.getEditDTO();
        editDTO.setCategory(null);
        editDTO.setAmount(null);

        doThrow(new IllegalArgumentException("Invalid data"))
                .when(mockService).update(any(TransactionEditDTO.class));

        mockMvc.perform(post(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(mapper.writeValueAsString(editDTO)))
                .andExpect(status().isBadRequest());
    }
}
