package org.example.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.app.domain.model.Goal;
import org.example.app.domain.repository.GoalRepository;
import org.example.app.domain.repository.impl.GoalRepositoryImpl;
import org.example.app.util.GoalGenerator;
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
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

@Testcontainers
@ExtendWith(MockitoExtension.class)
public class GoalServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("postgresTest")
            .withUsername("rootTest")
            .withPassword("passwordTest");

    private final GoalGenerator generator = new GoalGenerator();
    private GoalRepository goalRepository;
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
        goalRepository = new GoalRepositoryImpl(dataSource);

        // Создание таблицы и тестовых данных
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS financial_tracker");
            statement.execute("CREATE TABLE IF NOT EXISTS financial_tracker.goals " +
                    "(id SERIAL PRIMARY KEY, user_id INT NOT NULL , description TEXT, target_amount DECIMAL(10, 2) NOT NULL)");
        }
    }

    @Test
    @DisplayName("Should create a new goal and verify its properties")
    public void createGoalTest() {
        Goal goal = generator.getGoal();
        Long id = goalRepository.save(goal);

        Goal result = goalRepository.findById(id).get();
        String resultToString = result.toString();

        assertEquals(id, result.getId());
        assertEquals(goal.getDescription(), result.getDescription());
        assertTrue(resultToString.contains(goal.getTargetAmount().toString()));
    }

    @Test
    @DisplayName("Should retrieve all goals for a specific user")
    public void getAllGoalsByUserTest() {
        Goal create = generator.getGoal();
        create.setUserId(201L);
        Goal create2 = generator.getGoal();
        create2.setUserId(201L);

        goalRepository.save(create);
        goalRepository.save(create);

        List<Goal> results = goalRepository.findAllByUserId(201L);

        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    @DisplayName("Should update an existing goal's description")
    public void updateGoalTest() {
        Goal createGoal = generator.getGoal();

        Long id = goalRepository.save(createGoal);

        Goal editGoal = generator.getGoal();
        editGoal.setDescription("new description");
        editGoal.setId(id);

        goalRepository.update(editGoal);

        Goal results = goalRepository.findById(id).get();

        assertNotNull(results);
        assertEquals("new description", results.getDescription());
    }

    @Test
    @DisplayName("Should delete a goal and verify it's removed")
    public void deleteGoalTest() {
        Goal createGoal = generator.getGoal();
        Long id = goalRepository.save(createGoal);

        goalRepository.deleteById(id);

        Optional<Goal> results = goalRepository.findById(id);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when getting goals for non-existent user")
    public void getGoalsNotFoundUserTest() {
        Optional<Goal> results = goalRepository.findById(99999L);

        assertTrue(results.isEmpty());
    }

    @Test
    @DisplayName("Should create goal invalid data")
    public void createGoalInvalidDataTest() {
        Goal createGoal = generator.getGoal();
        createGoal.setTargetAmount(null);

        assertThrows(RuntimeException.class,
                () -> goalRepository.save(createGoal));
    }

    @Test
    @DisplayName("Should update goal with invalid data")
    public void updateGoalInvalidDataTest() {
        Goal createGoal = generator.getGoal();
        Long id = goalRepository.save(createGoal);

        Goal editGoal = generator.getGoal();
        editGoal.setId(id);
        editGoal.setTargetAmount(null);

        assertThrows(RuntimeException.class,
                () -> goalRepository.update(editGoal));
    }

    @Test
    @DisplayName("Should delete goal with ID not found")
    public void deleteGoalByIdNotFoundTest() {
        assertThrows(RuntimeException.class,
                () -> goalRepository.deleteById(99999L));
    }
}
