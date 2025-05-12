package org.example.app.application.dto.limit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Класс DTO (Data Transfer Object) для представления данных о лимите расходов.
 * Используется для передачи информации о лимите между слоями приложения.
 * Включает идентификатор лимита, идентификатор пользователя, значение лимита и флаг активности.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SpendingLimitDTO {

    /**
     * Уникальный идентификатор лимита расходов.
     */
    private Long id;

    /**
     * Идентификатор пользователя, связанного с лимитом расходов.
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
