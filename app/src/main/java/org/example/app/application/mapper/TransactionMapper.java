package org.example.app.application.mapper;

import org.example.app.application.dto.transaction.TransactionCreateDTO;
import org.example.app.application.dto.transaction.TransactionDTO;
import org.example.app.application.dto.transaction.TransactionEditDTO;
import org.example.app.domain.model.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

/**
 * Маппер для преобразования между DTO и сущностями транзакций.
 * <p>
 * Обеспечивает конвертацию между объектами разных слоев приложения:
 * <ul>
 *   <li>Transaction ↔ TransactionDTO (двустороннее преобразование)</li>
 *   <li>TransactionCreateDTO → Transaction (создание новой транзакции)</li>
 *   <li>TransactionEditDTO → Transaction (обновление существующей транзакции)</li>
 * </ul>
 * Автоматически генерирует реализацию с помощью MapStruct.
 * </p>
 */
@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {

    /**
     * Преобразует сущность Transaction в TransactionDTO.
     *
     * @param transaction сущность транзакции из доменного слоя
     * @return DTO транзакции для передачи между слоями
     */
    TransactionDTO map(Transaction transaction);

    /**
     * Преобразует TransactionCreateDTO в сущность Transaction.
     *
     * @param dto DTO для создания новой транзакции
     * @return сущность транзакции для сохранения в БД
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userId", source = "dto.userId")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "amount", source = "dto.amount")
    @Mapping(target = "date", source = "dto.date")
    @Mapping(target = "income", source = "dto.income")
    @Mapping(target = "category", source = "dto.category")
    Transaction map(TransactionCreateDTO dto);

    /**
     * Обновляет существующую транзакцию на основе данных из DTO.
     * <p>
     * Особенности обновления:
     * <ul>
     *   <li>Обновляются только те поля, которые явно указаны в DTO (не null)</li>
     *   <li>Флаг income обновляется всегда</li>
     *   <li>Исходная транзакция модифицируется (аннотация @MappingTarget)</li>
     * </ul>
     * </p>
     *
     * @param dto         DTO с обновленными данными
     * @param transaction сущность транзакции для обновления
     */
    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "userId", source = "dto.userId")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "amount", source = "dto.amount")
    @Mapping(target = "date", source = "dto.date")
    @Mapping(target = "income", source = "dto.income")
    @Mapping(target = "category", source = "dto.category")
    void update(TransactionEditDTO dto, @MappingTarget Transaction transaction);
}
