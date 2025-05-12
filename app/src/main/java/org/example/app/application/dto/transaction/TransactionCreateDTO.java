package org.example.app.application.dto.transaction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.app.domain.model.Category;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Класс DTO (Data Transfer Object) для создания новой транзакции.
 * Используется для передачи данных между слоями приложения при создании транзакции.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCreateDTO {

    /**
     * Идентификатор пользователя, связанного с транзакцией.
     */
    @NotNull
    private Long userId;

    /**
     * Сумма транзакции. Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    @NotNull
    private BigDecimal amount;

    /**
     * Категория транзакции. Определяет тип или группу, к которой относится транзакция.
     */
    @NotNull
    private Category category;

    /**
     * Описание транзакции. Содержит текстовую информацию о назначении или деталях транзакции.
     */
    @NotNull
    @NotBlank
    private String description;

    /**
     * Дата совершения транзакции. Используется тип {@link LocalDate} для представления даты.
     */
    @NotNull
    private LocalDate date;

    /**
     * Флаг, указывающий, является ли транзакция доходом (true) или расходом (false).
     */
    @NotNull
    private boolean income;
}
