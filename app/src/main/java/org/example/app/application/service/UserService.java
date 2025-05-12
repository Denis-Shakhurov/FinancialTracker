package org.example.app.application.service;

import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.AuthDTO;
import org.example.app.application.dto.user.UserCreateDTO;
import org.example.app.application.dto.user.UserDTO;
import org.example.app.application.dto.user.UserEditDTO;
import org.example.app.application.exception.ResourceNotFoundException;
import org.example.app.application.mapper.UserMapper;
import org.example.app.domain.model.User;
import org.example.app.domain.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления пользователями.
 * Предоставляет методы для работы с пользователями, такие как создание, обновление, удаление и поиск,
 * а также методы для управления статусом блокировки пользователей.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param dto Данные для создания нового пользователя.
     * @return Объект {@link UserDTO}, представляющий зарегистрированного пользователя.
     */
    public UserDTO register(UserCreateDTO dto) throws ResourceNotFoundException, IllegalArgumentException {

        User user = userMapper.map(dto);

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email "
                    + user.getEmail() + " already exists");
        }
        userRepository.save(user);
        return userMapper.map(user);
    }

    /**
     * Выполняет вход пользователя в систему.
     *
     * @param dto Содержит Электронную почту и пароль пользователя.
     * @return Объект {@link UserDTO}, представляющий авторизованного пользователя.
     */
    public UserDTO login(AuthDTO dto) {
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user != null && passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            return userMapper.map(user);
        } else {
            return null;
        }
    }

    /**
     * Обновляет данные пользователя.
     *
     * @param dto Данные для обновления пользователя.
     */
    public void update(UserEditDTO dto) throws ResourceNotFoundException {
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userMapper.update(dto, user);
        userRepository.update(user);
    }

    /**
     * Удаляет пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя, которого необходимо удалить.
     */
    public void delete(Long id) throws ResourceNotFoundException, IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userRepository.deleteById(user.getId());
    }

    /**
     * Возвращает данные пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return Объект {@link UserDTO}, представляющий пользователя.
     */
    public UserDTO get(Long id) throws ResourceNotFoundException, IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return userMapper.map(user);
    }

    /**
     * Возвращает список всех пользователей.
     *
     * @return Список объектов {@link UserDTO}, представляющих всех пользователей.
     */
    public List<UserDTO> getAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::map)
                .toList();
    }
}
