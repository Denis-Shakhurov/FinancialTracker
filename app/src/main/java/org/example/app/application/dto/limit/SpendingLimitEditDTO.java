package org.example.app.application.dto.limit;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Класс DTO (Data Transfer Object) для редактирования существующего лимита расходов.
 * Используется для передачи данных между слоями приложения при обновлении информации о лимите.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SpendingLimitEditDTO {

    /**
     * Уникальный идентификатор лимита расходов, который необходимо отредактировать.
     */
    @NotNull
    private Long id;

    /**
     * Идентификатор пользователя, связанного с лимитом расходов.
     */
    @NotNull
    private Long userId;

    /**
     * Новое значение лимита расходов. Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    @NotNull
    private BigDecimal limit;

    /**
     * Флаг, указывающий, активен ли лимит. Если true, лимит учитывается при расчетах, иначе — игнорируется.
     */
    @NotNull
    private boolean active;
}
