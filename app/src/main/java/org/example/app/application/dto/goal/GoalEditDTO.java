package org.example.app.application.dto.goal;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Класс DTO (Data Transfer Object) для редактирования существующей цели.
 * Используется для передачи данных между слоями приложения при обновлении информации о цели.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoalEditDTO {

    /**
     * Уникальный идентификатор цели, которую необходимо отредактировать.
     */
    private Long id;

    /**
     * Идентификатор пользователя, связанного с целью.
     */
    private Long userId;

    /**
     * Новое описание цели. Содержит обновленную текстовую информацию о цели.
     */
    @NotBlank
    private String description;

    /**
     * Новая целевая сумма, которую пользователь хочет накопить или достичь.
     * Используется тип {@link BigDecimal} для точного представления денежных значений.
     */
    private BigDecimal targetAmount;
}
