package org.example.app.application.dto.user;

import lombok.*;
import org.example.app.domain.model.Role;

/**
 * Класс DTO (Data Transfer Object) для передачи данных о пользователе.
 * Содержит информацию о пользователе, такую как идентификатор, имя, email, роль и статус блокировки.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя. Содержит полное имя или псевдоним пользователя.
     */
    private String name;

    /**
     * Электронная почта пользователя. Используется для идентификации и связи с пользователем.
     */
    private String email;

    /**
     * Роль пользователя. Определяет уровень доступа и права пользователя в системе.
     */
    private Role role;

    /**
     * Флаг, указывающий, заблокирован ли пользователь. Если true, пользователь не может войти в систему.
     */
    private boolean banned;
}
