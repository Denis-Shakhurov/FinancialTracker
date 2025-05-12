package org.example.app.domain.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * Класс, представляющий цель пользователя.
 * Содержит информацию о цели, такую как идентификатор, описание, целевая сумма и идентификатор пользователя.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Goal {

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
