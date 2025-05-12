package org.example.app.domain.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.example.app.domain.model.SpendingLimit;
import org.example.app.domain.repository.SpendingLimitRepository;
import org.example.app.utils.queries.SpendingLimitSqlQueries;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления лимитами расходов.
 * Реализует базовые операции CRUD (создание, чтение, обновление, удаление) для лимитов расходов.
 */
@Repository
@RequiredArgsConstructor
public class SpendingLimitRepositoryImpl implements SpendingLimitRepository {
    private final HikariDataSource dataSource;

    /**
     * Возвращает список всех активных лимитов расходов.
     *
     * @return список всех активных лимитов расходов
     */
    @Override
    public List<SpendingLimit> findAllActiveByUserId(Long userId) {
        List<SpendingLimit> limits = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     SpendingLimitSqlQueries.FIND_ALL_ACTIVE_BY_USER_ID)) {
            stmt.setBoolean(1, true);
            stmt.setLong(2, userId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    BigDecimal amount = rs.getBigDecimal("limit_amount");

                    SpendingLimit limit = new SpendingLimit();
                    limit.setId(id);
                    limit.setUserId(userId);
                    limit.setLimit(amount);
                    limit.setActive(true);

                    limits.add(limit);
                }
            }
            return limits;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Возвращает лимит расходов по идентификатору пользователя.
     *
     * @param id идентификатор пользователя
     * @return лимит расходов для указанного пользователя, или null, если лимит не найден
     */
    @Override
    public Optional<SpendingLimit> findById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     SpendingLimitSqlQueries.FIND_BY_ID)) {
            stmt.setLong(1, id);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long userId = rs.getLong("user_id");
                BigDecimal amount = rs.getBigDecimal("limit_amount");
                boolean active = rs.getBoolean("is_active");

                SpendingLimit limit = new SpendingLimit(id, userId, amount, active);
                return Optional.of(limit);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Сохраняет лимит расходов в репозитории.
     * Если лимит для указанного пользователя уже существует, он будет заменен.
     *
     * @param limit лимит расходов для сохранения
     */
    @Override
    public Long save(SpendingLimit limit) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    SpendingLimitSqlQueries.SAVE,
                    Statement.RETURN_GENERATED_KEYS)) {

                stmt.setLong(1, limit.getUserId());
                stmt.setBigDecimal(2, limit.getLimit());
                stmt.setBoolean(3, limit.isActive());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException("Failed to save spending limit - no rows affected");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        limit.setId(generatedKeys.getLong(1));
                        conn.commit();
                        return limit.getId();
                    } else {
                        conn.rollback();
                        throw new RuntimeException("DB did not return generated ID");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed to save spending limit", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save spending limit", e);
        }
    }

    /**
     * Обновляет лимит расходов в репозитории.
     * Если лимит для указанного пользователя уже существует, он будет заменен.
     *
     * @param limit лимит расходов с обновленными данными
     */
    @Override
    public void update(SpendingLimit limit) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(SpendingLimitSqlQueries.UPDATE)) {
                stmt.setLong(1, limit.getUserId());
                stmt.setBigDecimal(2, limit.getLimit());
                stmt.setBoolean(3, limit.isActive());
                stmt.setLong(4, limit.getId());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException(
                            String.format("No spending limit found with id: %d", limit.getId()));
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(
                        String.format("Failed to update spending limit with id: %d", limit.getId()), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Failed to update spending limit with id: %d", limit.getId()), e);
        }
    }

    /**
     * Удаляет лимит расходов по его идентификатору.
     *
     * @param id идентификатор лимита расходов для удаления
     */
    @Override
    public void deleteById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(SpendingLimitSqlQueries.DELETE_BY_ID)) {
                stmt.setLong(1, id);

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException(
                            String.format("No spending limit found with id: %d", id));
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(
                        String.format("Failed to delete spending limit with id: %d", id), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Failed to delete spending limit with id: %d", id), e);
        }
    }
}
