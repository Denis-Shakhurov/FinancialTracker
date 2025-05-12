package org.example.app.application.mapper;

import org.example.app.application.dto.limit.SpendingLimitCreateDTO;
import org.example.app.application.dto.limit.SpendingLimitDTO;
import org.example.app.application.dto.limit.SpendingLimitEditDTO;
import org.example.app.domain.model.SpendingLimit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между DTO и сущностями лимитов расходов.
 * <p>
 * Использует MapStruct для автоматического генерирования кода преобразования между:
 * <ul>
 *   <li>SpendingLimit <-> SpendingLimitDTO</li>
 *   <li>SpendingLimitCreateDTO -> SpendingLimit</li>
 *   <li>SpendingLimitEditDTO -> SpendingLimit (частичное обновление)</li>
 * </ul>
 * </p>
 */
@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface LimitMapper {

    /**
     * Преобразует сущность SpendingLimit в SpendingLimitDTO.
     *
     * @param limit сущность лимита расходов
     * @return DTO лимита расходов
     */
    SpendingLimitDTO map(SpendingLimit limit);

    /**
     * Преобразует SpendingLimitCreateDTO в сущность SpendingLimit.
     *
     * @param dto DTO для создания лимита
     * @return сущность лимита расходов
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "limit", source = "dto.limit")
    @Mapping(target = "userId", source = "dto.userId")
    @Mapping(target = "active", source = "dto.active")
    SpendingLimit map(SpendingLimitCreateDTO dto);

    /**
     * Обновляет сущность SpendingLimit на основе SpendingLimitEditDTO.
     * <p>
     * Особенности:
     * <ul>
     *   <li>Обновляет только не-null поля из DTO</li>
     *   <li>Всегда обновляет флаг активности</li>
     *   <li>Не изменяет поля, отсутствующие в DTO</li>
     * </ul>
     * </p>
     *
     * @param dto DTO с обновленными данными
     * @param limit сущность лимита для обновления (аннотация @MappingTarget указывает на обновляемую сущность)
     */
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "limit", source = "dto.limit")
    @Mapping(target = "userId", source = "dto.userId")
    @Mapping(target = "active", source = "dto.active")
    void update(SpendingLimitEditDTO dto, @MappingTarget SpendingLimit limit);
}
