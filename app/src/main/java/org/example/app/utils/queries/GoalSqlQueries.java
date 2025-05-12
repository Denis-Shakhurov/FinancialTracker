package org.example.app.utils.queries;

/**
 * Класс с SQL-запросами для работы с целями.
 */
public final class GoalSqlQueries {
    public static final String FIND_ALL = "SELECT * FROM financial_tracker.goals";
    public static final String FIND_BY_USER_ID = "SELECT * FROM financial_tracker.goals WHERE user_id = ?";
    public static final String FIND_BY_ID = "SELECT * FROM financial_tracker.goals WHERE id = ?";
    public static final String SAVE = "INSERT INTO financial_tracker.goals (user_id, description, target_amount) VALUES (?, ?, ?)";
    public static final String UPDATE = "UPDATE financial_tracker.goals SET description = ?, target_amount = ?, user_id = ? WHERE id = ?";
    public static final String DELETE_BY_ID = "DELETE FROM financial_tracker.goals WHERE id = ?";

    private GoalSqlQueries() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}
