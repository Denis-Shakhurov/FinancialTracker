package org.example.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.application.dto.AuthDTO;
import org.example.app.application.dto.user.UserCreateDTO;
import org.example.app.application.dto.user.UserDTO;
import org.example.app.application.dto.user.UserEditDTO;
import org.example.app.application.exception.ResourceNotFoundException;
import org.example.app.application.handler.GlobalExceptionHandler;
import org.example.app.application.service.UserService;
import org.example.app.presentation.controller.UserController;
import org.example.app.util.UserGenerator;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest {
    private static final String BASE_URL = "/api/users";
    private static final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserGenerator generator = new UserGenerator();

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this).close();
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/users/ returns a list of all users with 200 OK status")
    public void getAllUsersTest() throws Exception {
        // Arrange
        UserDTO userDTO = generator.getUserDTO();
        UserDTO userDTO2 = generator.getUserDTO();
        List<UserDTO> userList = Arrays.asList(userDTO, userDTO2);

        when(userService.getAll()).thenReturn(userList);

        // Assert
        mockMvc.perform(get(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value(userDTO.getEmail()))
                .andExpect(jsonPath("$[1].name").value(userDTO2.getName()));
    }

    @Test
    @DisplayName("GET /api/users/{id} returns a specific user with 200 OK status")
    public void getUserByIdTest() throws Exception {
        // Arrange
        UserDTO user = generator.getUserDTO();
        when(userService.get(user.getId())).thenReturn(user);

        // Assert
        mockMvc.perform(get(BASE_URL + "/{id}", user.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(user.getName()));
    }

    @Test
    @DisplayName("POST /api/users/register creates a new user and returns 201 CREATED with user data")
    public void createUserTest() throws Exception {
        // Arrange
        UserDTO userDTO = generator.getUserDTO();

        UserCreateDTO userCreateDTO = generator.getCreateDTO();
        when(userService.register(any(UserCreateDTO.class))).thenReturn(userDTO);

        // Assert
        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value(userDTO.getName()));
    }

    @Test
    @DisplayName("POST /api/users/login authenticates a user and returns 200 OK with user data")
    public void userLoginTest() throws Exception {
        // Arrange
        AuthDTO authDTO = new AuthDTO("john@example.com", "password123");
        UserDTO userDTO = generator.getUserDTO();

        when(userService.login(any(AuthDTO.class))).thenReturn(userDTO);

        // Assert
        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value(userDTO.getEmail()));
    }

    @Test
    @DisplayName("POST /api/users/logout logs out a user and returns 200 OK")
    public void userLogoutTest() throws Exception {

        mockMvc.perform(post(BASE_URL + "/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("User logout"));

    }

    @Test
    @DisplayName("POST /api/users/{id} updates a user and returns 200 OK")
    public void updateUserTest() throws Exception {
        UserEditDTO userEditDTO = generator.getEditDTO();

        doNothing().when(userService).update(any(UserEditDTO.class));

        mockMvc.perform(post(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(userEditDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("User updated successfully"));
    }

    @Test
    @DisplayName("DELETE /api/users/{id} deletes a user and returns 400 NO_CONTENT")
    public void deleteUserTest() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/users/{id} with wrong ID returns 404 NOT_FOUND")
    public void getUserByIdNotFoundTest() throws Exception {
        when(userService.get(1L)).thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound())
                .andExpect(content().string("User not found"));
    }

    @Test
    @DisplayName("GET /api/users/{id} with invalid ID returns 400 BAD REQUEST")
    public void getUserByIdInvalidIdTest() throws Exception {
        when(userService.get(1L)).thenThrow(new IllegalArgumentException("Invalid ID"));

        mockMvc.perform(get(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid ID"));
    }

    @Test
    public void createUserWithInvalidDataTest() throws Exception {
        UserCreateDTO createDTO = generator.getCreateDTO();
        createDTO.setEmail("email");

        doThrow(new IllegalArgumentException("Invalid data"))
                .when(userService).register(any(UserCreateDTO.class));

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users/register creates a new user with existing email and return 400 BAD_REQUEST")
    void createUserWithExistingEmailTest() throws Exception {
        UserDTO createdUser = generator.getUserDTO();

        doThrow(new IllegalArgumentException("Email already exists"))
                .when(userService).register(any(UserCreateDTO.class));

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(createdUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users/login user with invalid credentials returns 400 BAD_REQUEST")
    void loginUserWithInvalidCredentialsTest() throws Exception {
        AuthDTO authDTO = new AuthDTO("???", "password123");
        when(userService.login(any(AuthDTO.class)))
                .thenThrow(new IllegalArgumentException("Invalid credentials"));

        mockMvc.perform(post(BASE_URL + "/login")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(authDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/users/{id} updated a user with invalid data and returns 400 BAD_REQUEST")
    void updateUserWithInvalidDataTest() throws Exception {
        UserEditDTO userEditDTO = generator.getEditDTO();
        userEditDTO.setEmail(null);

        doThrow(new IllegalArgumentException("Invalid data"))
                .when(userService).update(any(UserEditDTO.class));

        mockMvc.perform(post(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(userEditDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data"));
    }

    @Test
    @DisplayName("POST /api/users/{id} deleted user with invalid id returns 400 BAD_REQUEST")
    void deleteUserWithInvalidIdTest() throws Exception {
        doThrow(new IllegalArgumentException("Invalid id"))
                .when(userService).delete(1L);

        mockMvc.perform(delete(BASE_URL + "/{id}", 1L)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid id"));
    }
}
