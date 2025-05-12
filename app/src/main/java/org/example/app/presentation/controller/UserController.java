package org.example.app.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.AuthDTO;
import org.example.app.application.dto.user.UserCreateDTO;
import org.example.app.application.dto.user.UserDTO;
import org.example.app.application.dto.user.UserEditDTO;
import org.example.app.application.service.UserService;
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
 * Контроллер для работы с пользователями.
 * Обрабатывает HTTP-запросы по пути "/api/users/*" и выполняет операции:
 * регистрацию, аутентификацию, управление пользователями.
 */
@Validated
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for managing users")
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get all users",
            description = "Returns a list of all registered users")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of users")
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        List<UserDTO> users = userService.getAll();
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Get user by ID",
            description = "Returns a single user by their ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> show(
            @Parameter(description = "ID of user to be retrieved", required = true)
            @PathVariable Long id) {
        UserDTO user = userService.get(id);
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Register new user",
            description = "Creates a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered user"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
            @Parameter(description = "User registration data", required = true)
            @RequestBody @Valid UserCreateDTO userCreateDTO,
            HttpServletResponse response) {
        UserDTO userDTO = userService.register(userCreateDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userDTO);
    }

    @Operation(summary = "User login",
            description = "Authenticates user and sets session cookie")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged in"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(
            @Parameter(description = "User credentials", required = true)
            @RequestBody @Valid AuthDTO authDTO,
            HttpServletResponse response) {
        UserDTO userDTO = userService.login(authDTO);
        return ResponseEntity.ok(userDTO);
    }

    @Operation(summary = "User logout",
            description = "Clears the user session cookie")
    @ApiResponse(responseCode = "200", description = "Successfully logged out")
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("User logout");
    }

    @Operation(summary = "Update user",
            description = "Updates user information")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated user"),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{id}")
    public ResponseEntity<String> update(
            @Parameter(description = "ID of user to be updated", required = true)
            @PathVariable Long id,
            @Parameter(description = "Updated user data", required = true)
            @RequestBody @Valid UserEditDTO userEditDTO) {
        userEditDTO.setId(id);
        userService.update(userEditDTO);
        return ResponseEntity.ok("User updated successfully");
    }

    @Operation(summary = "Delete user",
            description = "Deletes a user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted user"),
            @ApiResponse(responseCode = "400", description = "Invalid ID supplied"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID of user to be deleted", required = true)
            @PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
