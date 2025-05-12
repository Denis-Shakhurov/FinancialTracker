package org.example.app.domain.repository;

import org.example.app.domain.model.Goal;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с целями пользователей.
 * Предоставляет методы для поиска, сохранения, обновления и удаления целей.
 */
public interface GoalRepository {

    /**
     * Находит цель по её идентификатору.
     *
     * @param id Идентификатор цели.
     * @return {@link Optional}, содержащий найденную цель, или пустой {@link Optional}, если цель не найдена.
     */
    Optional<Goal> findById(Long id);

    /**
     * Находит все цели, связанные с определённым пользователем.
     *
     * @param userId Идентификатор пользователя.
     * @return Список целей, связанных с указанным пользователем.
     */
    List<Goal> findAllByUserId(Long userId);

    /**
     * Находит все цели.
     *
     * @return Список всех целей.
     */
    List<Goal> findAll();

    /**
     * Сохраняет новую цель.
     *
     * @param goal Цель для сохранения.
     * @return Идентификатор сохранённой цели.
     */
    Long save(Goal goal);

    /**
     * Обновляет данные существующей цели.
     *
     * @param goal Цель с обновлёнными данными.
     */
    void update(Goal goal);

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param id Идентификатор цели, которую необходимо удалить.
     */
    void deleteById(Long id);
}
