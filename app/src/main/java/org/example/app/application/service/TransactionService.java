package org.example.app.application.service;

import lombok.RequiredArgsConstructor;
import org.example.app.application.dto.transaction.TransactionCreateDTO;
import org.example.app.application.dto.transaction.TransactionDTO;
import org.example.app.application.dto.transaction.TransactionEditDTO;
import org.example.app.application.exception.ResourceNotFoundException;
import org.example.app.application.mapper.TransactionMapper;
import org.example.app.domain.model.Category;
import org.example.app.domain.model.Transaction;
import org.example.app.domain.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для управления транзакциями.
 * Предоставляет методы для работы с транзакциями, такие как создание, обновление, удаление и поиск,
 * а также методы для получения статистики по транзакциям.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    public TransactionDTO getById(Long id) throws ResourceNotFoundException, IllegalArgumentException {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid transaction id");
        }

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return transactionMapper.map(transaction);
    }

    /**
     * Возвращает список всех транзакций для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return список транзакций пользователя
     */
    public List<TransactionDTO> getAllByUserId(Long userId) {
        List<Transaction> transactions = transactionRepository.findAllByUserId(userId);
        return transactions.stream()
                .map(transactionMapper::map)
                .toList();
    }

    /**
     * Возвращает список всех транзакций.
     *
     * @return список всех транзакций.
     */
    public List<TransactionDTO> getAll() {
        List<Transaction> transactions = transactionRepository.findAll();
        return transactions.stream()
                .map(transactionMapper::map)
                .toList();
    }

    /**
     * Создает новую транзакцию.
     *
     * @param dto транзакция для создания
     */
    public Long create(TransactionCreateDTO dto) {

        Transaction transaction = transactionMapper.map(dto);

        return transactionRepository.save(transaction);
    }

    /**
     * Обновляет существующую транзакцию.
     *
     * @param dto транзакция с обновленными данными
     */
    public void update(TransactionEditDTO dto) throws ResourceNotFoundException {
        Transaction transaction = transactionRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transactionMapper.update(dto, transaction);
        transactionRepository.update(transaction);
    }

    /**
     * Удаляет транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции для удаления
     */
    public void delete(Long id) throws ResourceNotFoundException, IllegalArgumentException{
        if (id == null || id < 0) {
            throw new IllegalArgumentException("Invalid transaction id");
        }

        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transactionRepository.deleteById(transaction.getId());
    }

    /**
     * Возвращает сумму расходов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return сумма расходов пользователя
     */
    public BigDecimal getConsumptionByUserId(Long userId) {
        return transactionRepository.getConsumptionByUserId(userId);
    }

    /**
     * Возвращает сумму доходов для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return сумма доходов пользователя
     */
    public BigDecimal getIncomeByUserId(Long userId) {
        return transactionRepository.getIncomeByUserId(userId);
    }

    /**
     * Возвращает сумму расходов для указанного пользователя за текущий месяц.
     *
     * @param userId идентификатор пользователя
     * @return сумма расходов пользователя за текущий месяц
     */
    public BigDecimal getConsumptionByUserIdByMonth(Long userId) {
        return transactionRepository.getConsumptionByUserIdByMonth(userId);
    }

    /**
     * Возвращает текущий баланс для указанного пользователя.
     *
     * @param userId идентификатор пользователя
     * @return текущий баланс пользователя (доходы минус расходы)
     */
    public BigDecimal getBalanceByUserId(Long userId) {
        return transactionRepository.getBalanceByUserId(userId);
    }

    /**
     * Возвращает список транзакций для указанного пользователя за указанную дату.
     *
     * @param userId идентификатор пользователя
     * @param date дата транзакции
     * @return список транзакций пользователя за указанную дату
     */
    public List<TransactionDTO> getAllByUserIdByDate(Long userId, LocalDate date) {
        List<Transaction> transactions = transactionRepository.findAllByUserIdByDate(userId, date);
        return transactions.stream()
                .map(transactionMapper::map)
                .toList();
    }

    /**
     * Возвращает список транзакций для указанного пользователя по указанной категории.
     *
     * @param userId идентификатор пользователя
     * @param category категория транзакции
     * @return список транзакций пользователя по указанной категории
     */
    public List<TransactionDTO> getAllByUserIdByCategory(Long userId, Category category) {
        List<Transaction> transactions = transactionRepository.findAllByUserIdByCategory(userId, category);
        return transactions.stream()
                .map(transactionMapper::map)
                .toList();
    }

    /**
     * Возвращает список транзакций для указанного пользователя по типу (доход/расход).
     *
     * @param userId идентификатор пользователя
     * @param income true для доходов, false для расходов
     * @return список транзакций пользователя по указанному типу
     */
    public List<TransactionDTO> getAllByUserIdByIncome(Long userId, boolean income) {
        List<Transaction> transactions = transactionRepository.findAllByUserIdByIncome(userId, income);
        return transactions.stream()
                .map(transactionMapper::map)
                .toList();
    }

    /**
     * Возвращает сумму расходов для указанного пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return сумма расходов пользователя за указанный период
     */
    public BigDecimal getConsumptionByUserIdByPeriodDate(Long userId,
                                                         LocalDate startDate,
                                                         LocalDate endDate) {
        return transactionRepository.getConsumptionByUserIdByPeriodDate(userId, startDate, endDate);
    }

    /**
     * Возвращает сумму доходов для указанного пользователя за указанный период.
     *
     * @param userId идентификатор пользователя
     * @param startDate начальная дата периода
     * @param endDate конечная дата периода
     * @return сумма доходов пользователя за указанный период
     */
    public BigDecimal getIncomeByUserIdByPeriodDate(Long userId,
                                                    LocalDate startDate,
                                                    LocalDate endDate) {
        return transactionRepository.getIncomeByUserIdByPeriodDate(userId, startDate, endDate);
    }

    /**
     * Возвращает сумму расходов для указанного пользователя по указанной категории.
     *
     * @param userId идентификатор пользователя
     * @param category категория транзакции
     * @return сумма расходов пользователя по указанной категории
     */
    public BigDecimal getConsumptionByUserIdByCategory(Long userId, Category category) {
        return transactionRepository.getConsumptionByUserIdByCategory(userId, category);
    }
}
