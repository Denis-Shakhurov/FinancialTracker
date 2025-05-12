package org.example.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Класс, представляющий транзакцию.
 * Содержит информацию о транзакции, такую как сумма, категория, описание, дата и тип (доход/расход).
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

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
