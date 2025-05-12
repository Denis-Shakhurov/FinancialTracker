package org.example.app.application.service;

import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.limit.SpendingLimitDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * Сервис для отправки уведомлений.
 * Предоставляет функциональность для проверки превышения лимита расходов и отправки уведомлений.
 */
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SpendingLimitService spendingLimitService;
    /**
     * Проверяет, превышен ли лимит расходов для указанного пользователя.
     * Если лимит превышен, выводит предупреждение в консоль.
     *
     * @param userId идентификатор пользователя
     * @param consumption сумма расходов за текущий месяц
     */
    public void checkSpendingLimit(Long userId, BigDecimal consumption) {
        List<SpendingLimitDTO> limits = spendingLimitService.findAllByUserId(userId);

        for (SpendingLimitDTO limit : limits) {
            if (limit != null && consumption.compareTo(limit.getLimit()) > 0) {
                System.out.println("Превышен лимит расходов за текущий месяц!");
            }
        }
    }
}
