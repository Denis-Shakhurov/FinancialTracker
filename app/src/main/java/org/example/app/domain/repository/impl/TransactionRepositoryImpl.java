package org.example.app.domain.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.example.app.domain.model.Category;
import org.example.app.domain.model.Transaction;
import org.example.app.domain.repository.TransactionRepository;
import org.example.app.utils.queries.TransactionSqlQueries;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для управления транзакциями.
 * Реализует базовые операции CRUD (создание, чтение, обновление, удаление) для транзакций,
 * а также предоставляет дополнительные методы для работы с транзакциями.
 */
@Repository
@RequiredArgsConstructor
public class TransactionRepositoryImpl implements TransactionRepository {
    private final HikariDataSource dataSource;

    /**
     * Возвращает транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return транзакция с указанным идентификатором, или null, если транзакция не найдена
     */
    @Override
    public Optional<Transaction> findById(Long id) {

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(TransactionSqlQueries.FIND_BY_ID)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Long userId = rs.getLong("user_id");
                BigDecimal amount = rs.getBigDecimal("amount");
                LocalDate date = rs.getDate("date").toLocalDate();
                Category category = Category.valueOf(rs.getString("category"));
                String description = rs.getString("description");
                boolean isIncome = rs.getBoolean("is_income");

                Transaction transaction = new Transaction(id,
                        userId,
                        amount,
                        category,
                        description,
                        date,
                        isIncome);

                return Optional.of(transaction);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    /**
     * Возвращает список всех транзакций.
     *
     * @return список транзакций пользователя
     */
    @Override
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(TransactionSqlQueries.FIND_ALL)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                Long userId = rs.getLong("user_id");
                BigDecimal amount = rs.getBigDecimal("amount");
                LocalDate date = rs.getDate("date").toLocalDate();
                Category category = Category.valueOf(rs.getString("category"));
                String description = rs.getString("description");
                boolean isIncome = rs.getBoolean("is_income");

                Transaction transaction = new Transaction(id,
                        userId,
                        amount,
                        category,
                        description,
                        date,
                        isIncome);

                transactions.add(transaction);
            }
            rs.close();
            return transactions;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
    /**
     * Возвращает список всех транзакций для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список транзакций пользователя
     */
    @Override
    public List<Transaction> findAllByUserId(Long userId) {
        List<Transaction> transactions = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(TransactionSqlQueries.FIND_BY_USER_ID)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("id");
                BigDecimal amount = rs.getBigDecimal("amount");
                LocalDate date = rs.getDate("date").toLocalDate();
                Category category = Category.valueOf(rs.getString("category"));
                String description = rs.getString("description");
                boolean isIncome = rs.getBoolean("is_income");

                Transaction transaction = new Transaction(id,
                        userId,
                        amount,
                        category,
                        description,
                        date,
                        isIncome);

                transactions.add(transaction);
            }
            rs.close();
            return transactions;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * Сохраняет транзакцию в репозитории.
     *
     * @param transaction транзакция для сохранения
     */
    @Override
    public Long save(Transaction transaction) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    TransactionSqlQueries.SAVE,
                    Statement.RETURN_GENERATED_KEYS)) {

                stmt.setLong(1, transaction.getUserId());
                stmt.setBigDecimal(2, transaction.getAmount());
                stmt.setString(3, transaction.getCategory().name());
                stmt.setString(4, transaction.getDescription());
                stmt.setTimestamp(5, Timestamp.valueOf(transaction.getDate().atStartOfDay()));
                stmt.setBoolean(6, transaction.isIncome());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException("Failed to save transaction - no rows affected");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        transaction.setId(generatedKeys.getLong(1));
                        conn.commit();
                        return transaction.getId();
                    } else {
                        conn.rollback();
                        throw new RuntimeException("DB did not return generated ID");
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Failed to save transaction", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save transaction", e);
        }
    }

    /**
     * Обновляет транзакцию в репозитории.
     *
     * @param transaction транзакция с обновленными данными
     */
    @Override
    public void update(Transaction transaction) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(TransactionSqlQueries.UPDATE)) {
                stmt.setBigDecimal(1, transaction.getAmount());
                stmt.setTimestamp(2, Timestamp.valueOf(transaction.getDate().atStartOfDay()));
                stmt.setBoolean(3, transaction.isIncome());
                stmt.setString(4, transaction.getCategory().name());
                stmt.setString(5, transaction.getDescription());
                stmt.setLong(6, transaction.getUserId());
                stmt.setLong(7, transaction.getId());

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException(
                            String.format("No transaction found with id: %d", transaction.getId()));
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(
                        String.format("Failed to update transaction with id: %d", transaction.getId()), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Failed to update transaction with id: %d", transaction.getId()), e);
        }
    }

    /**
     * Удаляет транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции для удаления
     */
    @Override
    public void deleteById(Long id) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(TransactionSqlQueries.DELETE_BY_ID)) {
                stmt.setLong(1, id);

                if (stmt.executeUpdate() == 0) {
                    conn.rollback();
                    throw new RuntimeException(
                            String.format("No transaction found with id: %d", id));
                }

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(
                        String.format("Failed to delete transaction with id: %d", id), e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("Failed to delete transaction with id: %d", id), e);
        }
    }

    /**
     * Возвращает сумму расходов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return сумма расходов пользователя
     */
    @Override
    public BigDecimal getConsumptionByUserId(Long userId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(TransactionSqlQueries.GET_CONSUMPTION_BY_USER_ID)) {
            stmt.setLong(1, userId);
            stmt.setBoolean(2, false);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("result");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Возвращает сумму расходов для указанного пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return сумма расходов пользователя за указанный период
     */
    @Override
    public BigDecimal getConsumptionByUserIdByPeriodDate(Long userId, LocalDate startDate, LocalDate endDate) {
        try (Connection conn  = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     TransactionSqlQueries.GET_CONSUMPTION_BY_USER_ID_BY_PERIOD)) {
            stmt.setLong(1, userId);
            stmt.setBoolean(2, false);
            stmt.setTimestamp(3, Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setTimestamp(4, Timestamp.valueOf(endDate.atStartOfDay()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("result");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Возвращает сумму доходов для указанного пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return сумма доходов пользователя за указанный период
     */
    @Override
    public BigDecimal getIncomeByUserIdByPeriodDate(Long userId, LocalDate startDate, LocalDate endDate) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     TransactionSqlQueries.GET_INCOME_BY_USER_ID_BY_PERIOD)) {
            stmt.setLong(1, userId);
            stmt.setBoolean(2, true);
            stmt.setTimestamp(3, Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setTimestamp(4, Timestamp.valueOf(endDate.atStartOfDay()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("result");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Возвращает сумму доходов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return сумма доходов пользователя
     */
    @Override
    public BigDecimal getIncomeByUserId(Long userId) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     TransactionSqlQueries.GET_INCOME_BY_USER_ID)) {
            stmt.setLong(1, userId);
            stmt.setBoolean(2, true);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("result");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Возвращает сумму расходов для указанного пользователя за текущий месяц.
     *
     * @param userId идентификатор пользователя
     * @return сумма расходов пользователя за текущий месяц
     */
    @Override
    public BigDecimal getConsumptionByUserIdByMonth(Long userId) {
        int month = LocalDate.now().getMonthValue();
        int year = LocalDate.now().getYear();
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, 31);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareCall(
                     TransactionSqlQueries.GET_CONSUMPTION_BY_USER_ID_BY_MONTH)) {
            stmt.setLong(1, userId);
            stmt.setBoolean(2, false);
            stmt.setTimestamp(3, Timestamp.valueOf(startDate.atStartOfDay()));
            stmt.setTimestamp(4, Timestamp.valueOf(endDate.atStartOfDay()));

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("result");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }

    /**
     * Возвращает текущий баланс для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return текущий баланс пользователя (доходы минус расходы)
     */
    @Override
    public BigDecimal getBalanceByUserId(Long userId) {
        BigDecimal income = getIncomeByUserId(userId);
        BigDecimal consumption = getConsumptionByUserId(userId);

        if (income == null) {
            return BigDecimal.ZERO;
        } else {
            return income.subtract(consumption);
        }
    }

    /**
     * Возвращает список транзакций для указанного пользователя за указанную дату.
     *
     * @param userId идентификатор пользователя
     * @param date дата транзакции
     * @return список транзакций пользователя за указанную дату
     */
    @Override
    public List<Transaction> findAllByUserIdByDate(Long userId, LocalDate date) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     TransactionSqlQueries.FIND_BY_USER_ID_AND_DATE)) {
            stmt.setLong(1, userId);
            stmt.setTimestamp(2, Timestamp.valueOf(date.atStartOfDay()));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                BigDecimal amount = rs.getBigDecimal("amount");
                Category category = Category.valueOf(rs.getString("category"));
                String description = rs.getString("description");
                boolean isIncome = rs.getBoolean("is_income");

                Transaction transaction = new Transaction(id,
                        userId,
                        amount,
                        category,
                        description,
                        date,
                        isIncome);

                transactions.add(transaction);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Возвращает список транзакций для указанного пользователя по указанной категории.
     *
     * @param userId идентификатор пользователя
     * @param category категория транзакции
     * @return список транзакций пользователя по указанной категории
     */
    @Override
    public List<Transaction> findAllByUserIdByCategory(Long userId, Category category) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     TransactionSqlQueries.FIND_BY_USER_ID_AND_CATEGORY)) {
            stmt.setLong(1, userId);
            stmt.setString(2, category.name());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                BigDecimal amount = rs.getBigDecimal("amount");
                String description = rs.getString("description");
                LocalDate date = rs.getTimestamp("date").toLocalDateTime().toLocalDate();
                boolean isIncome = rs.getBoolean("is_income");

                Transaction transaction = new Transaction(id,
                        userId,
                        amount,
                        category,
                        description,
                        date,
                        isIncome);

                transactions.add(transaction);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Возвращает список транзакций для указанного пользователя по типу (доход/расход).
     *
     * @param userId идентификатор пользователя
     * @param isIncome true для доходов, false для расходов
     * @return список транзакций пользователя по указанному типу
     */
    @Override
    public List<Transaction> findAllByUserIdByIncome(Long userId, boolean isIncome) {
        List<Transaction> transactions = new ArrayList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     TransactionSqlQueries.FIND_BY_USER_ID_AND_INCOME)) {
            stmt.setLong(1, userId);
            stmt.setBoolean(2, isIncome);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Long id = rs.getLong("id");
                BigDecimal amount = rs.getBigDecimal("amount");
                Category category = Category.valueOf(rs.getString("category"));
                String description = rs.getString("description");
                LocalDate date = rs.getTimestamp("date").toLocalDateTime().toLocalDate();

                Transaction transaction = new Transaction(id,
                        userId,
                        amount,
                        category,
                        description,
                        date,
                        isIncome);

                transactions.add(transaction);
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    /**
     * Возвращает сумму расходов для указанного пользователя по указанной категории.
     *
     * @param userId идентификатор пользователя
     * @param category категория транзакции
     * @return сумма расходов пользователя по указанной категории
     */
    @Override
    public BigDecimal getConsumptionByUserIdByCategory(Long userId, Category category) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     TransactionSqlQueries.GET_CONSUMPTION_BY_USER_ID_AND_CATEGORY)) {
            stmt.setLong(1, userId);
            stmt.setString(2, category.name());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getBigDecimal("result");
            }
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}
