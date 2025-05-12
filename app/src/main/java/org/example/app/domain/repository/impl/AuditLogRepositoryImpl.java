package org.example.app.domain.repository.impl;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.example.app.domain.model.AuditLog;
import org.example.app.domain.repository.AuditLogRepository;
import org.example.app.utils.queries.AuditLogSqlQueries;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * Реализация репозитория для работы с записями аудита.
 * Обеспечивает сохранение записей аудита в базу данных.
 */
@Repository
@RequiredArgsConstructor
public class AuditLogRepositoryImpl implements AuditLogRepository {
    /**
     * Источник данных для подключения к базе данных
     */
    private final HikariDataSource dataSource;

    /**
     * Сохраняет запись аудита в базу данных.
     * Использует транзакцию для обеспечения атомарности операции.
     *
     * @param auditLog запись аудита для сохранения
     * @throws RuntimeException если произошла ошибка при сохранении записи
     */
    @Override
    public void save(AuditLog auditLog) {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            // Отключаем auto-commit для начала транзакции
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(
                    AuditLogSqlQueries.SAVE)) {

                stmt.setString(1, auditLog.getAction());
                stmt.setObject(2, auditLog.getUserId());
                stmt.setString(3, auditLog.getEmail());
                stmt.setString(4, auditLog.getDetails());
                stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

                stmt.executeUpdate();
                // Если все успешно - коммитим транзакцию
                conn.commit();
            } catch (SQLException e) {
                // В случае ошибки - откатываем транзакцию
                if (conn != null) {
                    conn.rollback();
                }
                throw new RuntimeException("Failed to save audit record", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save audit record", e);
        } finally {
            // Восстанавливаем auto-commit и закрываем соединение
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
}
