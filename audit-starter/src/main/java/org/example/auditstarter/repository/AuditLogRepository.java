package org.example.auditstarter.repository;

import org.example.auditstarter.model.AuditLog;

/**
 * Интерфейс репозитория для работы с записями аудита.
 * Определяет контракт для сохранения записей о действиях пользователей.
 */
public interface AuditLogRepository {
    /**
     * Сохраняет запись аудита в хранилище.
     * Реализация должна обеспечивать надежное сохранение информации
     * о действиях пользователей для последующего аудита.
     *
     * @param auditLog объект записи аудита, содержащий информацию о действии
     */
    void save(AuditLog auditLog);
}
