package org.example.app.application.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Класс DTO (Data Transfer Object) для создания новой цели.
 * Используется для передачи данных между слоями приложения.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalCreateDTO {

    /**
     * Идентификатор пользователя, для которого создается цель.
     */
    @NotNull
    private Long userId;

    /**
     * Описание цели. Может содержать текстовую информацию о том, что пользователь хочет достичь.
     */
    @NotNull
    @NotBlank
    private String description;

    /**
     * Целевая сумма, которую пользователь хочет накопить или достичь.
     * Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    @NotNull
    private BigDecimal targetAmount;
}
