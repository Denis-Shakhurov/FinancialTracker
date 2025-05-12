package org.example.auditstarter.config;

import com.zaxxer.hikari.HikariDataSource;
import org.example.auditstarter.aspect.AuditAspect;
import org.example.auditstarter.repository.AuditLogRepository;
import org.example.auditstarter.repository.AuditLogRepositoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для автоматического подключения аудита.
 * Активируется только при наличии в classpath класса AuditAspect.
 */
@Configuration
@ConditionalOnClass(AuditAspect.class)
public class AuditAutoConfiguration {

    /**
     * Создает бин аспекта аудита, если он отсутствует в контексте.
     *
     * @param auditLogRepository репозиторий для записи логов аудита
     * @return экземпляр аспекта аудита
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditAspect auditAspect(AuditLogRepository auditLogRepository) {
        return new AuditAspect(auditLogRepository);
    }

    /**
     * Создает бин репозитория для работы с логами аудита,
     * если он отсутствует в контексте.
     *
     * @param dataSource источник данных для подключения к БД
     * @return реализацию репозитория аудита
     */
    @Bean
    @ConditionalOnMissingBean
    public AuditLogRepository auditLogRepository(HikariDataSource dataSource) {
        return new AuditLogRepositoryImpl(dataSource);
    }
}
