package org.example.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.application.dto.limit.SpendingLimitCreateDTO;
import org.example.app.application.dto.limit.SpendingLimitDTO;
import org.example.app.application.dto.limit.SpendingLimitEditDTO;
import org.example.app.application.exception.ResourceNotFoundException;
import org.example.app.application.handler.GlobalExceptionHandler;
import org.example.app.application.service.SpendingLimitService;
import org.example.app.presentation.controller.SpendingLimitController;
import org.example.app.util.LimitGenerator;
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
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SpendingLimitControllerTest {
    private static final String BASE_URL = "/api/limits";
    private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;

    private MockMvc mockMvc;

    @Mock
    private SpendingLimitService mockService;

    @InjectMocks
    private SpendingLimitController controller;

    private final ObjectMapper mapper = new ObjectMapper();
    private final LimitGenerator generator = new LimitGenerator();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/limits/{id} returns a specific spending limit and status OK")
    public void getSpecificSpendingLimitByIdTest() throws Exception {
        SpendingLimitDTO limitDTO = generator.getSpendingLimitDTO();

        when(mockService.getById(1L)).thenReturn(limitDTO);

        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(limitDTO.getUserId()));
    }

    @Test
    @DisplayName("GET /api/limits/{id}/user returns all limits for a user and status OK")
    public void getAllLimitsForUserTest() throws Exception {
        SpendingLimitDTO limitDTO = generator.getSpendingLimitDTO();
        SpendingLimitDTO limitDTO2 = generator.getSpendingLimitDTO();

        List<SpendingLimitDTO> expectedLimits = Arrays.asList(
                limitDTO, limitDTO2
        );

        when(mockService.findAllByUserId(anyLong())).thenReturn(expectedLimits);

        mockMvc.perform(get(BASE_URL + "/{id}/user", 456L)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].limit").value(limitDTO.getLimit()))
                .andExpect(jsonPath("$[1].active").value(limitDTO2.isActive()));
    }

    @Test
    @DisplayName("POST /api/limits/ creates a new spending limit and return status 201 CREATED")
    public void createNewSpendingLimitTest() throws Exception {
        SpendingLimitCreateDTO createDTO = generator.getCreateDTO();

        when(mockService.create(any(SpendingLimitCreateDTO.class))).thenReturn(1L);

        mockMvc.perform(post(BASE_URL)
                .contentType(JSON_CONTENT_TYPE)
                .content(mapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$",containsString("created")));
    }

    @Test
    @DisplayName("POST /api/limits/{id} updated limit and returns status OK")
    public void updateLimitTest() throws Exception {
        SpendingLimitEditDTO editDTO = generator.getEditDTO();

        doNothing().when(mockService).update(any(SpendingLimitEditDTO.class));

        mockMvc.perform(post(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(mapper.writeValueAsString(editDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsString("updated")));
    }

    @Test
    @DisplayName("DELETE /api/limits{id} delete limit and returns 204 NO CONTENT")
    public void missingIdInDeleteRequestTest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/limits/{id} with invalid id returns status 400 BAD REQUEST")
    public void getInvalidIdTest() throws Exception {
        Long invalidId = -1L;
        when(mockService.getById(anyLong())).thenThrow(new IllegalArgumentException("Invalid id"));

        mockMvc.perform(get(BASE_URL + "/{id}", invalidId)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", containsString("Invalid id")));
    }

    @Test
    @DisplayName("GET /api/limits/{id} with id not limit returns status 404 NOT FOUND")
    public void getByIdWithIdNotFoundTest() throws Exception {
        when(mockService.getById(anyLong())).thenThrow(new ResourceNotFoundException("Limit not found"));

        mockMvc.perform(get(BASE_URL + "/{id}", 1000L)
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", containsString("Limit not found")));
    }

    @Test
    @DisplayName("POST /api/limits create limit with invalid data and returns status 400 BAD REQUEST")
    public void createLimitWithInvalidDataTest() throws Exception {
        SpendingLimitCreateDTO createDTO = generator.getCreateDTO();
        createDTO.setLimit(null);

        when(mockService.create(any(SpendingLimitCreateDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(mapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/limits/{id} updated limit with invalid data and returns status 400 BAD REQUEST")
    public void updateLimitWithInvalidDataTest() throws Exception {
        SpendingLimitEditDTO editDTO = generator.getEditDTO();
        editDTO.setLimit(null);

        doThrow(new IllegalArgumentException("Invalid data"))
                .when(mockService).update(any(SpendingLimitEditDTO.class));

        mockMvc.perform(post(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(mapper.writeValueAsString(editDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/limits/{id} delete limit with invalid id and returns status 404 NOT FOUND")
    public void deleteLimitWithInvalidIdTest() throws Exception {
        doThrow(new ResourceNotFoundException("Limit not found"))
                .when(mockService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Limit not found"));
    }
}
