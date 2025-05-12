package org.example.app.application.service;

import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.goal.GoalCreateDTO;
import org.example.app.application.dto.goal.GoalDTO;
import org.example.app.application.dto.goal.GoalEditDTO;
import org.example.app.application.exception.ResourceNotFoundException;
import org.example.app.application.mapper.GoalMapper;
import org.example.app.domain.model.Goal;
import org.example.app.domain.repository.GoalRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления целями.
 * Предоставляет методы для работы с целями, такие как создание, обновление, удаление и поиск.
 */
@Service
@RequiredArgsConstructor
public class GoalService {
    private final GoalRepository goalRepository;
    private final GoalMapper goalMapper;

    /**
     * Возвращает цель для указанного идентификатора.
     *
     * @param id идентификатор цели
     * @return цель
     */
    public GoalDTO getById(Long id) throws ResourceNotFoundException, IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));
        return goalMapper.map(goal);
    }

    /**
     * Возвращает список всех целей для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список целей пользователя
     */
    public List<GoalDTO> getAllByUser(Long userId) {
        List<Goal> goals = goalRepository.findAllByUserId(userId);

        return goals.stream()
                .map(goalMapper::map)
                .toList();
    }

    /**
     * Создает новую цель.
     *
     * @param dto цель для создания
     * @return идентификатор цели
     */
    public Long create(GoalCreateDTO dto) {
        Goal goal = goalMapper.map(dto);

        return goalRepository.save(goal);
    }

    /**
     * Обновляет существующую цель.
     * Если поля цели не указаны, используются значения из старой цели.
     *
     * @param dto цель с обновленными данными
     */
    public void update(GoalEditDTO dto) throws ResourceNotFoundException {
        Goal goal = goalRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goalMapper.update(dto, goal);
        goalRepository.update(goal);
    }

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param id идентификатор цели для удаления
     */
    public void delete(Long id) throws ResourceNotFoundException, IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }

        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goalRepository.deleteById(goal.getId());
    }
}
