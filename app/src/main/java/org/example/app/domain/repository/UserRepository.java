package org.example.app.domain.repository;

import org.example.app.domain.model.User;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с пользователями.
 * Предоставляет методы для поиска, сохранения, обновления и удаления пользователей.
 */
public interface UserRepository {

    /**
     * Находит всех пользователей.
     *
     * @return Список всех пользователей.
     */
    List<User> findAll();

    /**
     * Находит пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return {@link Optional}, содержащий найденного пользователя, или пустой {@link Optional}, если пользователь не найден.
     */
    Optional<User> findById(Long id);

    /**
     * Находит пользователя по его email.
     *
     * @param email Email пользователя.
     * @return {@link Optional}, содержащий найденного пользователя, или пустой {@link Optional}, если пользователь не найден.
     */
    Optional<User> findByEmail(String email);

    /**
     * Сохраняет нового пользователя.
     *
     * @param user Пользователь для сохранения.
     * @return Идентификатор сохранённого пользователя.
     */
    Long save(User user);

    /**
     * Обновляет данные существующего пользователя.
     *
     * @param user Пользователь с обновлёнными данными.
     */
    void update(User user);

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя, которого необходимо удалить.
     */
    void deleteById(Long id);
}
