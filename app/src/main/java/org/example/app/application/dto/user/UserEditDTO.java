package org.example.app.application.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.domain.model.Role;

/**
 * Класс DTO (Data Transfer Object) для редактирования данных пользователя.
 * Используется для передачи данных между слоями приложения при обновлении информации о пользователе.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserEditDTO {

    /**
     * Уникальный идентификатор пользователя, которого необходимо отредактировать.
     */
    private Long id;

    /**
     * Новое имя пользователя. Содержит полное имя или псевдоним пользователя.
     */
    @NotBlank
    private String name;

    /**
     * Новый адрес электронной почты пользователя. Используется для идентификации и связи с пользователем.
     */
    @Email
    private String email;

    /**
     * Новый пароль пользователя. Хранится в зашифрованном виде для обеспечения безопасности.
     */
    @NotBlank
    private String password;

    /**
     * Новая роль пользователя. Определяет уровень доступа и права пользователя в системе.
     */
    private Role role;

    /**
     * Флаг, указывающий, заблокирован ли пользователь. Если true, пользователь не может войти в систему.
     */
    private boolean banned;
}
