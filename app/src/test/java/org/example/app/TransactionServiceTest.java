package org.example.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.app.domain.model.Category;
import org.example.app.domain.model.Transaction;
import org.example.app.domain.repository.TransactionRepository;
import org.example.app.domain.repository.impl.TransactionRepositoryImpl;
import org.example.app.util.TransactionGenerator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.time.LocalDate;
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
public class TransactionServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("postgresTest")
            .withUsername("rootTest")
            .withPassword("passwordTest");

    private static TransactionGenerator generator = new TransactionGenerator();
    private static TransactionRepository repository;
    private static HikariDataSource dataSource;
    private static HikariConfig config;

    @BeforeAll
    public static void setUp() throws Exception {
        config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());

        dataSource = new HikariDataSource(config);
        // Инициализация репозитория и сервиса
        repository = new TransactionRepositoryImpl(dataSource);

        // Создание таблицы и тестовых данных
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS financial_tracker");
            statement.execute("CREATE TABLE IF NOT EXISTS financial_tracker.transactions " +
                    "(id SERIAL PRIMARY KEY, user_id INT NOT NULL , amount DECIMAL(10, 2) NOT NULL , " +
                    "category VARCHAR(100) NOT NULL , description TEXT, date TIMESTAMP, is_income BOOLEAN)");
        }

        for (Transaction transaction : generator.getTransactionList()) {
            repository.save(transaction);
        }
    }

    @Test
    @DisplayName("Should create a transaction and verify its properties")
    public void creteTransactionTest() {
        Transaction create = generator.getTransaction();
        create.setIncome(false);
        Long id = repository.save(create);

        Transaction result = repository.findById(id).get();
        String resultToString = result.toString();

        assertEquals(create.getDescription(), result.getDescription());
        assertFalse(result.isIncome());

        assertTrue(resultToString.contains(create.getCategory().name()));
    }

    @Test
    @DisplayName("Should retrieve all transactions for a specific user")
    public void getAllTransactionsByUserIdTest() {
        Long userId = 333L;
        Transaction create = generator.getTransaction();
        create.setUserId(userId);

        Transaction create2 = generator.getTransaction();
        create2.setUserId(userId);

        repository.save(create);
        repository.save(create2);

        List<Transaction> result = repository.findAllByUserId(userId);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should update transaction description")
    public void updateTransactionTest() {
        Transaction create = generator.getTransaction();
        Long id = repository.save(create);

        Transaction edit = generator.getTransaction();
        edit.setId(id);
        edit.setDescription("description");

        repository.update(edit);

        Transaction result = repository.findById(id).get();

        assertEquals("description", result.getDescription());
    }

    @Test
    @DisplayName("Should delete a transaction and verify it's removed")
    public void deleteTransactionTest() {
        Transaction create = generator.getTransaction();

        Long id = repository.save(create);
        repository.deleteById(id);

        Optional<Transaction> result = repository.findById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list for non-existent user")
    public void getTransactionByWrongUserIdTest() {
        List<Transaction> result = repository.findAllByUserId(2000L);

        assertEquals(result, new ArrayList<>());
    }

    @Test
    @DisplayName("Should get transaction return current data")
    public void getTransactionByIdTest() {
        Transaction create = generator.getTransaction();
        Long id = repository.save(create);

        Transaction result = repository.findById(id).get();

        assertEquals(create.getDescription(), result.getDescription());
    }

    @Test
    @DisplayName("Should create transaction with invalid data")
    public void createTransactionWithInvalidDataTest() {
        Transaction create = generator.getTransaction();
        create.setAmount(null);

        assertThrows(RuntimeException.class,
                () -> repository.save(create));
    }

    @Test
    @DisplayName("Should update transaction with invalid data")
    public void updateTransactionWithInvalidDataTest() {
        Transaction create = generator.getTransaction();
        Long id = repository.save(create);

        Transaction edit = generator.getTransaction();
        edit.setId(id);
        edit.setUserId(null);

        assertThrows(RuntimeException.class,
                () -> repository.update(edit));
    }

    @Test
    @DisplayName("Should not found transaction")
    public void getNotFoundTransactionTest() {
        Optional<Transaction> result = repository.findById(99999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should delete transaction not found")
    public void deleteNotFoundTransactionTest() {
        assertThrows(RuntimeException.class,
                () -> repository.deleteById(99999L));
    }

    @Test
    @DisplayName("Should calculate total consumption for a user")
    public void getConsumptionByUserIdTest() {
        Transaction create = generator.getTransaction();
        Transaction create2 = generator.getTransaction();

        create.setUserId(1001L);
        create.setIncome(false);
        create.setAmount(BigDecimal.valueOf(1500.0));

        create2.setUserId(1001L);
        create2.setIncome(false);
        create2.setAmount(BigDecimal.valueOf(1000.0));

        repository.save(create);
        repository.save(create2);

        BigDecimal result = repository.getConsumptionByUserId(1001L);

        assertNotNull(result);
        assertEquals(new BigDecimal(2500.00).setScale(2), result);
    }

    @Test
    @DisplayName("Should calculate consumption for a user within date range")
    public void getConsumptionByUserIdByPeriodDateTest() {
        LocalDate startDate = LocalDate.of(2025, 3, 6);
        LocalDate endDate = LocalDate.of(2025, 3, 10);

        BigDecimal result = repository.getConsumptionByUserIdByPeriodDate(1234L, startDate, endDate);

        assertEquals(new BigDecimal(100.00).setScale(2), result);
    }

    @Test
    @DisplayName("Should calculate income for a user within date range")
    public void getIncomeByUserIdByPeriodDateTest() {
        LocalDate startDate = LocalDate.of(2025, 2, 20);
        LocalDate endDate = LocalDate.of(2025, 2, 28);

        BigDecimal result = repository.getIncomeByUserIdByPeriodDate(1234L, startDate, endDate);

        assertEquals(new BigDecimal(50000.00).setScale(2), result);
    }

    @Test
    @DisplayName("Should calculate total income for a user")
    public void getIncomeByUserIdTest() {
        BigDecimal result = repository.getIncomeByUserId(1234L);

        assertEquals(new BigDecimal(50000.00).setScale(2), result);
    }

    @Test
    @DisplayName("Should calculate balance for a user (income - consumption)")
    public void getBalanceByUserIdTest() {
        BigDecimal result = repository.getBalanceByUserId(1234L);

        assertEquals(new BigDecimal(47500.00).setScale(2), result);
    }

    @Test
    @DisplayName("Should find transactions for a user on specific date")
    public void findAllByUserIdByDateTest() {
        LocalDate date = LocalDate.of(2025, 3, 1);
        Long id = 1234L;

        List<Transaction> result = repository.findAllByUserIdByDate(id, date);

        assertNotNull(result);
        assertEquals(new BigDecimal(1200).setScale(2), result.get(0).getAmount());
    }

    @Test
    @DisplayName("Should find transactions for a user by category")
    public void findAllByUserIdByCategoryTest() {
        List<Transaction> result = repository.findAllByUserIdByCategory(1234L, Category.HOUSE);

        assertNotNull(result);
        assertEquals(new BigDecimal(1200).setScale(2), result.get(0).getAmount());
    }

    @Test
    @DisplayName("Should find income/expense transactions for a user")
    public void findAllByUserIdByIncomeTest() {
        List<Transaction> result = repository.findAllByUserIdByIncome(1234L, true);

        assertNotNull(result);
        assertEquals(new BigDecimal(50000).setScale(2), result.get(0).getAmount());
    }

    @Test
    @DisplayName("Should calculate consumption for a user by category")
    public void getConsumptionByUserIdByCategoryTest() {
        BigDecimal result = repository.getConsumptionByUserIdByCategory(1234L, Category.PRODUCTS);

        assertEquals(new BigDecimal(1300.00).setScale(2), result);
    }
}
