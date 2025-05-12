package org.example.app.application.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.domain.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Класс DTO (Data Transfer Object) для редактирования существующей транзакции.
 * Используется для передачи данных между слоями приложения при обновлении информации о транзакции.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEditDTO {

    /**
     * Уникальный идентификатор транзакции, которую необходимо отредактировать.
     */
    private Long id;

    /**
     * Идентификатор пользователя, связанного с транзакцией.
     */
    private Long userId;

    /**
     * Новая сумма транзакции. Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    private BigDecimal amount;

    /**
     * Новая категория транзакции. Определяет тип или группу, к которой относится транзакция.
     */
    private Category category;

    /**
     * Новое описание транзакции. Содержит обновленную текстовую информацию о назначении или деталях транзакции.
     */
    @NotBlank
    private String description;

    /**
     * Новая дата совершения транзакции. Используется тип {@link LocalDate} для представления даты.
     */
    private LocalDate date;

    /**
     * Флаг, указывающий, является ли транзакция доходом (true) или расходом (false).
     */
    private boolean income;
}
