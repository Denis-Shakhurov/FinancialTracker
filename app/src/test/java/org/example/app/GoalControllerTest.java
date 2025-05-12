package org.example.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.application.dto.goal.GoalCreateDTO;
import org.example.app.application.dto.goal.GoalDTO;
import org.example.app.application.dto.goal.GoalEditDTO;
import org.example.app.application.exception.ResourceNotFoundException;
import org.example.app.application.handler.GlobalExceptionHandler;
import org.example.app.application.service.GoalService;
import org.example.app.presentation.controller.GoalController;
import org.example.app.util.GoalGenerator;
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
import static org.mockito.ArgumentMatchers.any;
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

public class GoalControllerTest {
    private static final String BASE_URL = "/api/goals";
    private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;

    private MockMvc mockMvc;

    @Mock
    private GoalService goalService;

    @InjectMocks
    private GoalController goalController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GoalGenerator generator = new GoalGenerator();

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        mockMvc = MockMvcBuilders.standaloneSetup(goalController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/goals/{id} goal by ID returns correct goal data and 200 status")
    public void getGoalByIdTest() throws Exception {
        GoalDTO expectedGoal = generator.getGoalDTO();

        when(goalService.getById(anyLong())).thenReturn(expectedGoal);

        mockMvc.perform(get(BASE_URL+ "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(expectedGoal.getId()))
                .andExpect(jsonPath("$.description").value(expectedGoal.getDescription()));
    }

    @Test
    @DisplayName("GET /api/goals/{id}/user returns all goals for a user and status OK")
    public void getGoalByIdUserTest() throws Exception {
        GoalDTO goalDTO = generator.getGoalDTO();
        GoalDTO goalDTO2 = generator.getGoalDTO();

        List<GoalDTO> expectedGoals = Arrays.asList(
                goalDTO, goalDTO2
        );

        when(goalService.getAllByUser(anyLong())).thenReturn(expectedGoals);

        mockMvc.perform(get(BASE_URL + "/{id}/user", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].description").value(goalDTO.getDescription()))
                .andExpect(jsonPath("$[1].targetAmount").value(goalDTO2.getTargetAmount()));
    }

    @Test
    @DisplayName("POST /api/goals/ creating a new goal returns status 201 CREATED")
    public void createGoalTest() throws Exception {
        GoalCreateDTO goalCreateDTO = generator.getCreateDTO();

        when(goalService.create(any(GoalCreateDTO.class))).thenReturn(1L);

        mockMvc.perform(post(BASE_URL)
        .contentType(JSON_CONTENT_TYPE)
                .content(objectMapper.writeValueAsString(goalCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", containsString("created")));
    }

    @Test
    @DisplayName("POST /api/goals/{id} update goal and returns status 200 OK")
    public void updateGoalTest() throws Exception {
        GoalEditDTO editDTO = generator.getEditDTO();

        doNothing().when(goalService).update(any(GoalEditDTO.class));

        mockMvc.perform(post(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(editDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", containsString("updated")));

    }

    @Test
    @DisplayName("DELETE /api/goals/{id} delete goal and returns status 204 NO CONTENT")
    public void deleteGoalTest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/goals/{id} get goals with ID not found and returns status 404 NO_FOUND")
    public void getGoalByIdNotFoundTest() throws Exception {
        when(goalService.getById(anyLong()))
                .thenThrow(new ResourceNotFoundException("Goal not found"));

        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$", containsString("Goal not found")));
    }

    @Test
    @DisplayName("GET /api/goals/{id} get goal with invalid ID and returns 400 BAD_REQUEST")
    public void getGoalByIdInvalidIdTest() throws Exception {
        when(goalService.getById(anyLong()))
                .thenThrow(new IllegalArgumentException("Invalid ID"));

        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$", containsString("Invalid ID")));
    }

    @Test
    @DisplayName("POST /api/goals created goal with invalid data and returns 400 BAD_REQUEST")
    public void createGoalWithInvalidDataTest() throws Exception {
        GoalCreateDTO goalCreateDTO = generator.getCreateDTO();
        goalCreateDTO.setDescription(null);
        goalCreateDTO.setTargetAmount(null);

        when(goalService.create(any(GoalCreateDTO.class))).
                thenThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(goalCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/goals/{id} updated goal with invalid data and returns 400 BAD_REQUEST")
    public void updateGoalWithInvalidDataTest() throws Exception {
        GoalEditDTO editDTO = generator.getEditDTO();
        editDTO.setDescription(null);
        editDTO.setTargetAmount(null);

        doThrow(new IllegalArgumentException("Invalid data"))
                .when(goalService).update(any(GoalEditDTO.class));

        mockMvc.perform(post(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(editDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/goals/{id} deleted goal with invalid ID and returns 400 BAD_REQUEST")
    public void deleteGoalWithInvalidIdTest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid ID"))
                .when(goalService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/{id}", -1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID"));
    }
}
