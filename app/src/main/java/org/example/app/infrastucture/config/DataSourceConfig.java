package org.example.app.infrastucture.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Класс конфигурации источника данных для подключения к базе данных.
 * Использует HikariCP как пул соединений и автоматически создает схему БД при инициализации.
 */
@Configuration
public class DataSourceConfig {
    // SQL для создания схемы базы данных
    private final String CREATE_SCHEMA_FT = "CREATE SCHEMA IF NOT EXISTS financial_tracker;";

    /**
     * Создает и настраивает источник данных HikariCP.
     * Читает параметры подключения из конфигурационного файла,
     * создает пул соединений и инициализирует схему базы данных.
     *
     * @return настроенный источник данных HikariCP
     * @throws RuntimeException если не удалось создать схему базы данных
     * @see HikariDataSource
     */

    @Bean
    @Primary
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties dataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public HikariDataSource dataSource(DataSourceProperties properties) {
        HikariDataSource dataSource = properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class)
                .build();

        initializeSchema(dataSource);
        return dataSource;
    }

    private void initializeSchema(DataSource dataSource) {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_SCHEMA_FT);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create schema", e);
        }
    }
}
