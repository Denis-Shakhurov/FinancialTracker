package org.example.app.utils.queries;

/**
 * Класс с SQL-запросами для работы с лимитами расходов.
 */
public final class SpendingLimitSqlQueries {
    public static final String FIND_ALL_ACTIVE_BY_USER_ID =
            "SELECT * FROM financial_tracker.spending_limits WHERE is_active = ? AND user_id = ?";
    public static final String FIND_BY_ID =
            "SELECT * FROM financial_tracker.spending_limits WHERE id = ?";
    public static final String SAVE =
            "INSERT INTO financial_tracker.spending_limits (user_id, limit_amount, is_active) VALUES (?, ?, ?)";
    public static final String UPDATE =
            "UPDATE financial_tracker.spending_limits SET user_id = ?, limit_amount = ?, is_active = ? WHERE id = ?";
    public static final String DELETE_BY_ID =
            "DELETE FROM financial_tracker.spending_limits WHERE id = ?";

    private SpendingLimitSqlQueries() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}
