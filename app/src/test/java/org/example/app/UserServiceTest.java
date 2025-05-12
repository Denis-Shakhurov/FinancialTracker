package org.example.app;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.example.app.domain.model.User;
import org.example.app.domain.repository.UserRepository;
import org.example.app.domain.repository.impl.UserRepositoryImpl;
import org.example.app.util.UserGenerator;
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
public class UserServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("postgresTest")
            .withUsername("rootTest")
            .withPassword("passwordTest");

    private final UserGenerator userGenerator = new UserGenerator();
    private HikariDataSource dataSource;
    private HikariConfig config;
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() throws Exception {
        config = new HikariConfig();
        config.setJdbcUrl(postgres.getJdbcUrl());
        config.setUsername(postgres.getUsername());
        config.setPassword(postgres.getPassword());

        dataSource = new HikariDataSource(config);
        // Инициализация репозитория и сервиса
        userRepository = new UserRepositoryImpl(dataSource);

        // Создание таблицы и тестовых данных
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.execute("CREATE SCHEMA IF NOT EXISTS financial_tracker");
            statement.execute("CREATE TABLE IF NOT EXISTS financial_tracker.users " +
                    "(id SERIAL PRIMARY KEY, name VARCHAR(100), email VARCHAR(100) NOT NULL, " +
                    "password VARCHAR(100) NOT NULL, role VARCHAR(30), is_banned BOOLEAN)");
        }
    }

    @Test
    @DisplayName("Should register a new user and verify properties")
    public void registerTest() {
        User createUser = userGenerator.getUser();

        Long id = userRepository.save(createUser);

        User result = userRepository.findById(id).get();
        String resultToString = result.toString();

        assertEquals(createUser.getEmail(), result.getEmail());
        assertTrue(resultToString.contains(createUser.getName()));
    }

    @Test
    @DisplayName("Should retrieve all users")
    public void getAllUsersTest() {
        User createUser = userGenerator.getUser();
        User createUser2 = userGenerator.getUser();

        userRepository.save(createUser);
        userRepository.save(createUser2);

        List<User> results = userRepository.findAll();

        assertNotNull(results);
    }

    @Test
    @DisplayName("Should retrieve user by ID")
    public void getUserByIdTest() {
        User createUser = userGenerator.getUser();

        Long id = userRepository.save(createUser);

        User result = userRepository.findById(id).get();

        assertNotNull(result);
        assertEquals(createUser.getName(), result.getName());
    }

    @Test
    @DisplayName("Should update user information")
    public void updateUserTest() {
        User createUser = userGenerator.getUser();

        Long id = userRepository.save(createUser);

        User editUser = userGenerator.getUser();
        editUser.setId(id);

        userRepository.update(editUser);

        User result = userRepository.findById(id).get();

        assertEquals(editUser.getName(), result.getName());
    }

    @Test
    @DisplayName("Should delete user and verify removal")
    public void deleteUserTest() {
        User createUser = userGenerator.getUser();

        Long id = userRepository.save(createUser);

        userRepository.deleteById(id);

        Optional<User> result = userRepository.findById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should find user by email")
    public void getUserByEmailTest() {
        User createUser = userGenerator.getUser();

        userRepository.save(createUser);

        User result = userRepository.findByEmail(createUser.getEmail()).get();

        assertEquals(createUser.getName(), result.getName());
    }

    @Test
    @DisplayName("Should return null when getting non-existent user by id")
    public void getByIdNotFoundUserTest() {
        Optional<User> result = userRepository.findById(99999L);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return null when getting non-existent user by email")
    public void getByEmailNotFoundUserTest() {
        Optional<User> result = userRepository.findByEmail("non-existent@email.com");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should fail get user with invalid ID")
    public void getUserWithInvalidIDTest() {

        assertThrows(RuntimeException.class,
                () -> userRepository.findById(null));
    }

    @Test
    @DisplayName("Should fail update user with invalid data")
    public void updateUserWithInvalidDataTest() {
        User createUser = userGenerator.getUser();

        Long id = userRepository.save(createUser);

        User editUser = userGenerator.getUser();
        editUser.setId(id);
        editUser.setPassword(null);

        assertThrows(RuntimeException.class,
                () -> userRepository.update(editUser));
    }

    @Test
    @DisplayName("Should delete user with invalid ID")
    public void deleteUserWithInvalidIDTest() {

        assertThrows(RuntimeException.class,
                () -> userRepository.deleteById(9999L));
    }
}
