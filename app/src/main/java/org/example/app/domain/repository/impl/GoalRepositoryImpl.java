package org.example.app.domain.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.example.app.domain.model.Goal;
import org.example.app.domain.repository.GoalRepository;
import org.example.app.utils.queries.GoalSqlQueries;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления целями.
 * Реализует базовые операции CRUD (создание, чтение, обновление, удаление) для целей,
 * а также предоставляет дополнительные методы для работы с целями.
 */
@Repository
@RequiredArgsConstructor
public class GoalRepositoryImpl implements GoalRepository {
    private final HikariDataSource dataSource;

    /**
     * Возвращает список всех целей.
     *
     * @return список целей пользователя
     */
    @Override
    public List<Goal> findAll() {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GoalSqlQueries.FIND_ALL)) {
            ResultSet rs = stmt.executeQuery();

            List<Goal> goals = new ArrayList<>();
            while (rs.next()) {
                long id = rs.getLong("id");
                long userId = rs.getLong("user_id");
                String description = rs.getString("description");
                BigDecimal targetAmount = rs.getBigDecimal("target_amount");

                Goal goal = new Goal();
                goal.setId(id);
                goal.setUserId(userId);
                goal.setDescription(description);
                goal.setTargetAmount(targetAmount);

                goals.add(goal);
            }
            rs.close();
            return goals;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Возвращает список целей для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список целей пользователя
     */
    @Override
    public List<Goal> findAllByUserId(Long userId) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GoalSqlQueries.FIND_BY_USER_ID)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            List<Goal> goals = new ArrayList<>();
            while (rs.next()) {
                long id = rs.getLong("id");
                String description = rs.getString("description");
                BigDecimal targetAmount = rs.getBigDecimal("target_amount");

                Goal goal = new Goal();
                goal.setId(id);
                goal.setUserId(userId);
                goal.setDescription(description);
                goal.setTargetAmount(targetAmount);

                goals.add(goal);
            }
            rs.close();
            return goals;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Возвращает цель по её идентификатору.
     *
     * @param id идентификатор цели
     * @return цель с указанным идентификатором, или null, если цель не найдена
     */
    @Override
    public Optional<Goal> findById(Long id) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(GoalSqlQueries.FIND_BY_ID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long goalId = rs.getLong("id");
                String description = rs.getString("description");
                BigDecimal amount = rs.getBigDecimal("target_amount");

                Goal goal = new Goal(id, goalId, description, amount);
                return Optional.of(goal);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Сохраняет цель в репозитории.
     *
     * @param goal цель для сохранения
     * @return идентификатор цели
     */
    @Override
    public Long save(Goal goal) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    GoalSqlQueries.SAVE, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setLong(1, goal.getUserId());
                stmt.setString(2, goal.getDescription());
                stmt.setBigDecimal(3, goal.getTargetAmount());

                stmt.executeUpdate();

                try (ResultSet generatedKey = stmt.getGeneratedKeys()) {
                    if (generatedKey.next()) {
                        goal.setId(generatedKey.getLong(1));
                        conn.commit();
                        return goal.getId();
                    } else {
                        conn.rollback();
                        throw new SQLException("DB have not returned an id after saving an entity");
                    }
                }
            }
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    e.addSuppressed(ex);
                }
            }
            throw new RuntimeException("Failed to save goal", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    throw new RuntimeException("Failed to close connection", e);
                }
            }
        }
    }

    /**
     * Обновляет цель в репозитории.
     *
     * @param goal цель с обновленными данными
     */
    @Override
    public void update(Goal goal) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(GoalSqlQueries.UPDATE)) {
                stmt.setString(1, goal.getDescription());
                stmt.setBigDecimal(2, goal.getTargetAmount());
                stmt.setLong(3, goal.getUserId());
                stmt.setLong(4, goal.getId());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException("Update failed, no rows affected for goal id: " + goal.getId());
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed to update goal with id: " + goal.getId(), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update goal with id: " + goal.getId(), e);
        }
    }

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param id идентификатор цели для удаления
     */
    @Override
    public void deleteById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(GoalSqlQueries.DELETE_BY_ID)) {
                stmt.setLong(1, id);

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException("No goal found with id: " + id);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed to delete goal with id: " + id, e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete goal with id: " + id, e);
        }
    }
}
