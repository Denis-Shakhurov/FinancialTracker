package org.example.app.application.mapper;

import jakarta.validation.Valid;
import org.example.app.application.dto.goal.GoalCreateDTO;
import org.example.app.application.dto.goal.GoalDTO;
import org.example.app.application.dto.goal.GoalEditDTO;
import org.example.app.domain.model.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между DTO и сущностями целей.
 * <p>
 * Использует MapStruct для автоматического генерирования кода преобразования.
 * Обеспечивает конвертацию между:
 * <ul>
 *   <li>Goal <-> GoalDTO</li>
 *   <li>GoalCreateDTO -> Goal</li>
 *   <li>GoalEditDTO -> Goal (частичное обновление)</li>
 * </ul>
 * </p>
 */
@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface GoalMapper {

    /**
     * Преобразует сущность Goal в GoalDTO.
     *
     * @param goal сущность цели
     * @return DTO цели
     */
    GoalDTO map(Goal goal);

    /**
     * Преобразует GoalCreateDTO в сущность Goal.
     *
     * @param dto DTO для создания цели
     * @return сущность цели
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "dto.userId")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "targetAmount", source = "dto.targetAmount")
    Goal map(GoalCreateDTO dto);

    /**
     * Обновляет сущность Goal на основе данных из GoalEditDTO.
     * <p>
     * Выполняет частичное обновление - изменяет только те поля, которые не null в DTO.
     * </p>
     *
     * @param dto DTO с обновленными данными
     * @param goal сущность цели для обновления
     */
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "userId", source = "dto.userId")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "targetAmount", source = "dto.targetAmount")
    Goal update(@Valid GoalEditDTO dto, @MappingTarget Goal goal);
}
