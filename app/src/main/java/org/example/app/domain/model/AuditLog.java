package org.example.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Класс, представляющий запись в журнале аудита (журнале событий).
 * Содержит информацию о действиях пользователей в системе.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    /**
     * Уникальный идентификатор записи в журнале аудита
     */
    private Long id;

    /**
     * Действие, которое было выполнено (например, "LOGIN", "CREATE_USER", etc.)
     */
    private String action;

    /**
     * Идентификатор пользователя, выполнившего действие
     */
    private Long userId;

    /**
     * Email пользователя, выполнившего действие
     */
    private String email;

    /**
     * Дополнительные детали о выполненном действии
     */
    private String details;

    /**
     * Дата и время, когда было выполнено действие
     */
    private LocalDateTime timestamp;
}
