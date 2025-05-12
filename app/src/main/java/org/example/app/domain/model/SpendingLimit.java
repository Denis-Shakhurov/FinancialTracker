package org.example.app.domain.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * Класс, представляющий лимит расходов пользователя.
 * Содержит информацию о лимите расходов, идентификаторе пользователя и идентификаторе лимита.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SpendingLimit {

    /**
     * Уникальный идентификатор цели.
     */
    private Long id;

    /**
     * Идентификатор пользователя, связанного с целью.
     */
    private Long userId;

    /**
     * Значение лимита расходов. Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    private BigDecimal limit;

    /**
     * Флаг, указывающий, активен ли лимит. Если true, лимит учитывается при расчетах, иначе — игнорируется.
     */
    private boolean active;
}
