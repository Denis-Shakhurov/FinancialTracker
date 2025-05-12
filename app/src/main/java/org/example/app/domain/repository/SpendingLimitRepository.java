package org.example.app.domain.repository;

import org.example.app.domain.model.SpendingLimit;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с лимитами расходов.
 * Предоставляет методы для поиска, сохранения, обновления и удаления лимитов расходов.
 */
public interface SpendingLimitRepository {

    /**
     * Находит лимит расходов по его идентификатору.
     *
     * @param id Идентификатор лимита расходов.
     * @return {@link Optional}, содержащий найденный лимит расходов, или пустой {@link Optional}, если лимит не найден.
     */
    Optional<SpendingLimit> findById(Long id);

    /**
     * Находит все активные лимиты расходов, связанные с определённым пользователем.
     *
     * @param userId Идентификатор пользователя.
     * @return Список активных лимитов расходов, связанных с указанным пользователем.
     */
    List<SpendingLimit> findAllActiveByUserId(Long userId);

    /**
     * Сохраняет новый лимит расходов.
     *
     * @param spendingLimit Лимит расходов для сохранения.
     * @return Идентификатор сохранённого лимита расходов.
     */
    Long save(SpendingLimit spendingLimit);

    /**
     * Обновляет данные существующего лимита расходов.
     *
     * @param spendingLimit Лимит расходов с обновлёнными данными.
     */
    void update(SpendingLimit spendingLimit);

    /**
     * Удаляет лимит расходов по его идентификатору.
     *
     * @param id Идентификатор лимита расходов, который необходимо удалить.
     */
    void deleteById(Long id);
}
