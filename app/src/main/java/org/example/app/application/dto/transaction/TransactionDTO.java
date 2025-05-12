package org.example.app.application.dto.transaction;

import lombok.*;
import org.example.app.domain.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Класс DTO (Data Transfer Object) для представления данных о транзакции.
 * Используется для передачи информации о транзакции между слоями приложения.
 * Включает идентификатор транзакции, идентификатор пользователя, сумму, категорию, описание, дату и флаг дохода/расхода.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionDTO {

    /**
     * Уникальный идентификатор транзакции.
     */
    private Long id;

    /**
     * Идентификатор пользователя, связанного с транзакцией.
     */
    private Long userId;

    /**
     * Сумма транзакции. Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    private BigDecimal amount;

    /**
     * Категория транзакции. Определяет тип или группу, к которой относится транзакция.
     */
    private Category category;

    /**
     * Описание транзакции. Содержит текстовую информацию о назначении или деталях транзакции.
     */
    private String description;

    /**
     * Дата совершения транзакции. Используется тип {@link LocalDate} для представления даты.
     */
    private LocalDate date;

    /**
     * Флаг, указывающий, является ли транзакция доходом (true) или расходом (false).
     */
    private boolean income;
}
