package org.example.app.utils.queries;

/**
 * Класс с SQL-запросами для работы с аудит-логами
 */
public final class AuditLogSqlQueries {
    public static final String SAVE =
            "INSERT INTO financial_tracker.audit_logs (action, user_id, email, details, timestamp) " +
                    "VALUES (?, ?, ?, ?, ?)";

    private AuditLogSqlQueries() {
        // Приватный конструктор для предотвращения создания экземпляров класса
    }
}
