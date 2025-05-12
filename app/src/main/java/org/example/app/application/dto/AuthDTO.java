package org.example.app.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) для аутентификации пользователя.
 * Содержит учетные данные, необходимые для входа пользователя в систему.
 * Используется для передачи данных аутентификации между слоями приложения.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthDTO {
    /**
     * Электронная почта пользователя, используемая как идентификатор для входа.
     * <p>
     * Обязательное поле. Должна быть в формате email и содержать символ '@'.
     * Должна соответствовать существующему аккаунту пользователя.
     * </p>
     */
    @NotNull
    @Email
    private String email;

    /**
     * Пароль пользователя для аутентификации.
     * <p>
     * Обязательное поле. Должен совпадать с паролем в системе.
     * Должен передаваться безопасным способом (рекомендуется HTTPS).
     * Сравнивается с хешированным паролем в базе данных.
     * </p>
     */
    @NotNull
    @NotBlank
    private String password;
}
