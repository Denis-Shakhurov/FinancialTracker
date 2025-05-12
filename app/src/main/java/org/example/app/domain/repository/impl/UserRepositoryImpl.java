package org.example.app.domain.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.example.app.domain.model.Role;
import org.example.app.domain.model.User;
import org.example.app.domain.repository.UserRepository;
import org.example.app.utils.queries.UserSqlQueries;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления пользователями.
 * Реализует базовые операции CRUD (создание, чтение, обновление, удаление) для пользователей,
 * а также предоставляет дополнительные методы для работы с пользователями.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final HikariDataSource dataSource;

    /**
     * Возвращает список всех пользователей.
     *
     * @return список всех пользователей
     */
    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     UserSqlQueries.FIND_ALL)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                boolean isBanned = rs.getBoolean("is_banned");

                User user = new User(id, name, email, password, role, isBanned);
                users.add(user);
            }
            rs.close();
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Возвращает пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя
     * @return пользователь с указанным идентификатором, или null, если пользователь не найден
     */
    @Override
    public Optional<User> findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     UserSqlQueries.FIND_BY_ID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String email = rs.getString("email");
                String password = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                boolean isBanned = rs.getBoolean("is_banned");

                User user = new User(id, name, email, password, role, isBanned);

                return Optional.of(user);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Сохраняет пользователя в репозитории.
     *
     * @param user пользователь для сохранения
     */
    @Override
    public Long save(User user) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    UserSqlQueries.SAVE,
                    Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRole().name());
                stmt.setBoolean(5, user.isBanned());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException("Failed to save user - no rows affected");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        user.setId(generatedKeys.getLong(1));
                        conn.commit();
                        return user.getId();
                    } else {
                        conn.rollback();
                        throw new RuntimeException("DB did not return generated ID");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed to save user", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save user", e);
        }
    }

    /**
     * Обновляет пользователя в репозитории.
     *
     * @param user пользователь с обновленными данными
     */
    @Override
    public void update(User user) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    UserSqlQueries.UPDATE)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRole().name());
                stmt.setBoolean(5, user.isBanned());
                stmt.setLong(6, user.getId());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException(
                            String.format("No user found with id: %d", user.getId()));
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(
                        String.format("Failed to update user with id: %d", user.getId()), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Failed to update user with id: %d", user.getId()), e);
        }
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id идентификатор пользователя для удаления
     */
    @Override
    public void deleteById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    UserSqlQueries.DELETE_BY_ID)) {
            stmt.setLong(1, id);

            if (stmt.executeUpdate() == 0) {
                conn.rollback();
                throw new RuntimeException(
                        String.format("No user found with id: %d", id));
            }
            conn.commit();
        } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(
                        String.format("Failed to delete user with id: %d", id), e);
        }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Failed to delete transaction with id: %d", id), e);
        }
    }

    /**
     * Возвращает пользователя по его email.
     *
     * @param email email пользователя
     * @return Optional, содержащий пользователя, если он найден, иначе пустой Optional
     */
    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     UserSqlQueries.FIND_BY_EMAIL)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Long id = rs.getLong("id");
                String name = rs.getString("name");
                String password = rs.getString("password");
                Role role = Role.valueOf(rs.getString("role"));
                boolean isBanned = rs.getBoolean("is_banned");

                User user = new User(id, name, email, password, role, isBanned);

                return Optional.of(user);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
