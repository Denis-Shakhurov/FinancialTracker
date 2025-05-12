package org.example.app.application.dto.limit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Класс DTO (Data Transfer Object) для создания нового лимита расходов.
 * Используется для передачи данных между слоями приложения при создании лимита.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpendingLimitCreateDTO {

    /**
     * Идентификатор пользователя, для которого устанавливается лимит расходов.
     */
    @NotNull
    private Long userId;

    /**
     * Значение лимита расходов. Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    @NotNull
    private BigDecimal limit;

    /**
     * Флаг, указывающий, активен ли лимит. Если true, лимит учитывается при расчетах, иначе — игнорируется.
     */
    @NotNull
    private boolean active;
}
