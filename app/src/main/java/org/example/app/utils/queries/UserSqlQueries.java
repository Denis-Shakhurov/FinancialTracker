package org.example.app.utils.queries;

/**
 * Класс с SQL-запросами для работы с пользователями.
 */
public final class UserSqlQueries {
    public static final String FIND_ALL = "SELECT * FROM financial_tracker.users";
    public static final String FIND_BY_ID = "SELECT * FROM financial_tracker.users WHERE id = ?";
    public static final String SAVE = "INSERT INTO financial_tracker.users (name, email, password, role, is_banned) " +
            "VALUES (?, ?, ?, ?, ?)";
    public static final String UPDATE = "UPDATE financial_tracker.users " +
            "SET name = ?, email = ?, password = ?, role = ?, is_banned = ? " +
            "WHERE id = ?";
    public static final String DELETE_BY_ID = "DELETE FROM financial_tracker.users WHERE id = ?";
    public static final String FIND_BY_EMAIL = "SELECT * FROM financial_tracker.users WHERE email = ?";

    private UserSqlQueries() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}