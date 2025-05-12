package org.example.app.application.service;

import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.limit.SpendingLimitCreateDTO;
import org.example.app.application.dto.limit.SpendingLimitDTO;
import org.example.app.application.dto.limit.SpendingLimitEditDTO;
import org.example.app.application.exception.ResourceNotFoundException;
import org.example.app.application.mapper.LimitMapper;
import org.example.app.domain.model.SpendingLimit;
import org.example.app.domain.repository.SpendingLimitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Сервис для управления лимитами расходов.
 * Предоставляет методы для установки и получения лимитов расходов пользователей.
 */
@Service
@RequiredArgsConstructor
public class SpendingLimitService {
    private final SpendingLimitRepository spendingLimitRepository;
    private final LimitMapper limitMapper;

    /**
     * Возвращает лимит расходов по идентификатору.
     *
     * @param id идентификатор лимита
     * @return лимитов расходов
     */
    public SpendingLimitDTO getById(Long id) throws ResourceNotFoundException, IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }

        SpendingLimit limit = spendingLimitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spending limit not found"));

        return limitMapper.map(limit);
    }

    /**
     * Устанавливает лимит расходов для пользователя и возвращает id лимита
     *
     * @param dto объект лимита расходов для сохранения
     * @return id лимита
     */
    public Long create(SpendingLimitCreateDTO dto) {

        SpendingLimit limit = limitMapper.map(dto);

        return spendingLimitRepository.save(limit);
    }

    /**
     * Возвращает лимит расходов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список лимитов расходов пользователя
     */
    public List<SpendingLimitDTO> findAllByUserId(Long userId) {
        List<SpendingLimit> limits = spendingLimitRepository.findAllActiveByUserId(userId);
        return limits.stream()
                .map(limitMapper::map)
                .toList();
    }

    /**
     * Обновляет существующий лимит.
     *
     * @param dto лимит с обновленными данными
     */
    public void update(SpendingLimitEditDTO dto) throws ResourceNotFoundException, IllegalArgumentException {
        SpendingLimit limit = spendingLimitRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Spending limit not found"));

        limitMapper.update(dto, limit);
        spendingLimitRepository.update(limit);
    }

    /**
     * Удаляет лимит по её идентификатору.
     *
     * @param id идентификатор лимита для удаления
     */
    public void delete(Long id) throws ResourceNotFoundException, IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid id");
        }

        SpendingLimit limit = spendingLimitRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Spending limit not found"));

        spendingLimitRepository.deleteById(limit.getId());
    }
}
