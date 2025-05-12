package org.example.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.app.domain.model.SpendingLimit;
import org.example.app.domain.repository.SpendingLimitRepository;
import org.example.app.domain.repository.impl.SpendingLimitRepositoryImpl;
import org.example.app.util.LimitGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@Testcontainers
@ExtendWith(MockitoExtension.class)
public class SpendingLimitServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("postgresTest")
            .withUsername("rootTest")
            .withPassword("passwordTest");

    private final LimitGenerator generator = new LimitGenerator();
    private SpendingLimitRepository repository;
    private HikariDataSource dataSource;
    private HikariConfig config;

    @BeforeEach
    public void setUp() throws Exception {
        config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());

        dataSource = new HikariDataSource(config);
        // Инициализация репозитория и сервиса
        repository = new SpendingLimitRepositoryImpl(dataSource);

        // Создание таблицы и тестовых данных
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS financial_tracker");
            statement.execute("CREATE TABLE IF NOT EXISTS financial_tracker.spending_limits " +
                    "(id SERIAL PRIMARY KEY, user_id INT NOT NULL , limit_amount DECIMAL(10, 2) NOT NULL , is_active BOOLEAN NOT NULL )");
        }
    }

    @Test
    @DisplayName("Should create a new spending limit and verify its properties")
    public void createSpendingLimitTest() {
        SpendingLimit createLimit = generator.getLimit();
        createLimit.setActive(true);

        Long id = repository.save(createLimit);

        SpendingLimit result = repository.findById(id).get();
        String resultToString = result.toString();

        assertTrue(result.isActive());
        assertTrue(resultToString.contains(createLimit.getLimit().toString()));
        assertEquals(createLimit.getUserId(), result.getUserId());
    }

    @Test
    @DisplayName("Should retrieve all active spending limits for a specific user")
    public void getAllSpendingLimitsByUserTest() {
        SpendingLimit createLimit1 = generator.getLimit();
        createLimit1.setUserId(100L);
        createLimit1.setActive(true);
        SpendingLimit createLimit2 = generator.getLimit();
        createLimit2.setUserId(100L);
        createLimit2.setActive(false);

        repository.save(createLimit1);
        repository.save(createLimit2);

        List<SpendingLimit> result = repository.findAllActiveByUserId(100L);

        assertTrue(result.size() == 1);
        assertTrue(result.get(0).isActive());
    }

    @Test
    @DisplayName("Should delete a spending limit and verify it's removed")
    public void deleteSpendingLimitTest() {
        SpendingLimit createDTO1 = generator.getLimit();
        createDTO1.setUserId(10001L);

        Long id = repository.save(createDTO1);
        assertNotNull(repository.findById(id));

        repository.deleteById(id);

        List<SpendingLimit> result = repository.findAllActiveByUserId(1001L);

        assertEquals(result, new ArrayList<>());
    }

    @Test
    @DisplayName("Should return empty list when getting limits for non-existent user")
    public void getAllSpendingLimitsWithWrongUserIdTest() {
        List<SpendingLimit> result = repository.findAllActiveByUserId(200L);

        assertEquals(result, new ArrayList<>());
    }

    @Test
    @DisplayName("Should update spending limit's active status")
    public void updateSpendingLimitTest() {
        SpendingLimit createLimit = generator.getLimit();
        createLimit.setActive(true);

        Long id = repository.save(createLimit);

        SpendingLimit editLimit = generator.getLimit();
        editLimit.setId(id);
        editLimit.setActive(false);

        repository.update(editLimit);

        SpendingLimit result = repository.findById(id).get();

        assertFalse(result.isActive());
    }

    @Test
    @DisplayName("Should retrieve spending limit by ID and verify user association")
    public void getSpendingLimitTest() {
        SpendingLimit createLimit = generator.getLimit();
        createLimit.setUserId(33L);

        Long id = repository.save(createLimit);

        SpendingLimit result = repository.findById(id).get();

        assertEquals(createLimit.getUserId(), result.getUserId());
    }

    @Test
    @DisplayName("Should get limit not found")
    public void getLimitNotFoundTest() {
        Optional<SpendingLimit> limit = repository.findById(99999L);

        assertTrue(limit.isEmpty());
    }

    @Test
    @DisplayName("Should create limit with invalid data")
    public void createLimitWithInvalidDataTest() {
        SpendingLimit createLimit = generator.getLimit();
        createLimit.setLimit(null);

        assertThrows(RuntimeException.class,
                () -> repository.save(createLimit));
    }

    @Test
    @DisplayName("Should update limit with invalid data")
    public void updateLimitWithInvalidDataTest() {
        SpendingLimit createLimit = generator.getLimit();

        Long id = repository.save(createLimit);

        SpendingLimit editLimit = generator.getLimit();
        editLimit.setId(id);
        editLimit.setLimit(null);

        assertThrows(RuntimeException.class,
                () -> repository.update(editLimit));
    }

    @Test
    @DisplayName("Should delete limit not found")
    public void deleteLimitNotFoundTest() {
        assertThrows(RuntimeException.class,
                () -> repository.deleteById(99999L));
    }
}
