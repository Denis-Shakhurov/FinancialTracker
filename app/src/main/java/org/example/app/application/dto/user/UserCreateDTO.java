package org.example.app.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.domain.model.Role;

/**
 * Класс DTO (Data Transfer Object) для создания нового пользователя.
 * Используется для передачи данных между слоями приложения при создании пользователя.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDTO {

    /**
     * Имя пользователя. Содержит полное имя или псевдоним пользователя.
     */
    @NotNull
    @NotBlank
    private String name;

    /**
     * Электронная почта пользователя. Используется для идентификации и связи с пользователем.
     */
    @NotNull
    @Email
    private String email;

    /**
     * Пароль пользователя. Хранится в зашифрованном виде для обеспечения безопасности.
     */
    @NotNull
    @NotBlank
    private String password;

    /**
     * Роль пользователя. Определяет уровень доступа и права пользователя в системе.
     */
    @NotNull
    private Role role;

    /**
     * Флаг, указывающий, заблокирован ли пользователь. Если true, пользователь не может войти в систему.
     */
    private boolean banned;
}
