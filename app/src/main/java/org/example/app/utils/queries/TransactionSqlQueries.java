package org.example.app.utils.queries;

/**
 * Класс с SQL-запросами для работы с транзакциями.
 */
public final class TransactionSqlQueries {
    public static final String FIND_ALL = "SELECT * FROM financial_tracker.transactions";
    public static final String FIND_BY_ID = "SELECT * FROM financial_tracker.transactions WHERE id = ?";
    public static final String FIND_BY_USER_ID = "SELECT * FROM financial_tracker.transactions WHERE user_id = ?";
    public static final String SAVE = "INSERT INTO financial_tracker.transactions " +
            "(user_id, amount, category, description, date, is_income) VALUES (?, ?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE financial_tracker.transactions " +
            "SET amount = ?, date = ?, is_income = ?, category = ?, description = ?, user_id = ? " +
            "WHERE id = ?";
    public static final String DELETE_BY_ID = "DELETE FROM financial_tracker.transactions " +
            "WHERE id = ?";

    public static final String GET_CONSUMPTION_BY_USER_ID = "SELECT SUM(amount) AS result " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND is_income = ?";
    public static final String GET_CONSUMPTION_BY_USER_ID_BY_PERIOD = "SELECT SUM(amount) AS result " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND is_income = ? " +
            "AND date BETWEEN ? AND ?";
    public static final String GET_INCOME_BY_USER_ID_BY_PERIOD = "SELECT SUM(amount) AS result " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND is_income = ? " +
            "AND date BETWEEN ? AND ?";
    public static final String GET_INCOME_BY_USER_ID = "SELECT SUM(amount) AS result " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND is_income = ?";
    public static final String GET_CONSUMPTION_BY_USER_ID_BY_MONTH = "SELECT SUM(amount) AS result " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND is_income = ? " +
            "AND date BETWEEN ? AND ?";

    public static final String FIND_BY_USER_ID_AND_DATE = "SELECT * " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND date = ?";
    public static final String FIND_BY_USER_ID_AND_CATEGORY = "SELECT * " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND category = ?";
    public static final String FIND_BY_USER_ID_AND_INCOME = "SELECT * " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND is_income = ?";
    public static final String GET_CONSUMPTION_BY_USER_ID_AND_CATEGORY = "SELECT SUM(amount) AS result " +
            "FROM financial_tracker.transactions " +
            "WHERE user_id = ? " +
            "AND category = ?";

    private TransactionSqlQueries() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}
