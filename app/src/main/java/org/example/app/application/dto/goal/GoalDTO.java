package org.example.app.application.dto.goal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * Класс DTO (Data Transfer Object) для представления данных о цели.
 * Используется для передачи информации о цели между слоями приложения.
 * Включает идентификатор цели, идентификатор пользователя, описание и целевую сумму.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GoalDTO {

    /**
     * Уникальный идентификатор цели.
     */
    private Long id;

    /**
     * Идентификатор пользователя, связанного с целью.
     */
    private Long userId;

    /**
     * Описание цели. Содержит текстовую информацию о том, что пользователь хочет достичь.
     */
    private String description;

    /**
     * Целевая сумма, которую пользователь хочет накопить или достичь.
     * Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    private BigDecimal targetAmount;
}
