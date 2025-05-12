package org.example.app.domain.repository;

import org.example.app.domain.model.Category;
import org.example.app.domain.model.Transaction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий для работы с транзакциями.
 * Предоставляет методы для поиска, сохранения, обновления и удаления транзакций,
 * а также для получения финансовых данных, таких как расходы, доходы и баланс.
 */
public interface TransactionRepository {

    /**
     * Находит транзакцию по её идентификатору.
     *
     * @param id Идентификатор транзакции.
     * @return {@link Optional}, содержащий найденную транзакцию, или пустой {@link Optional}, если транзакция не найдена.
     */
    Optional<Transaction> findById(Long id);

    /**
     * Находит все транзакции, связанные с определённым пользователем.
     *
     * @param userId Идентификатор пользователя.
     * @return Список транзакций, связанных с указанным пользователем.
     */
    List<Transaction> findAllByUserId(Long userId);

    /**
     * Находит все транзакции.
     *
     * @return Список всех транзакций.
     */
    List<Transaction> findAll();

    /**
     * Сохраняет новую транзакцию.
     *
     * @param transaction Транзакция для сохранения.
     * @return Идентификатор сохранённой транзакции.
     */
    Long save(Transaction transaction);

    /**
     * Обновляет данные существующей транзакции.
     *
     * @param transaction Транзакция с обновлёнными данными.
     */
    void update(Transaction transaction);

    /**
     * Удаляет транзакцию по её идентификатору.
     *
     * @param id Идентификатор транзакции, которую необходимо удалить.
     */
    void deleteById(Long id);

    /**
     * Возвращает общую сумму расходов пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Сумма расходов пользователя в виде {@link BigDecimal}.
     */
    BigDecimal getConsumptionByUserId(Long userId);

    /**
     * Возвращает сумму расходов пользователя за указанный период.
     *
     * @param userId    Идентификатор пользователя.
     * @param startDate Начальная дата периода.
     * @param endDate   Конечная дата периода.
     * @return Сумма расходов пользователя за указанный период в виде {@link BigDecimal}.
     */
    BigDecimal getConsumptionByUserIdByPeriodDate(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Возвращает сумму доходов пользователя за указанный период.
     *
     * @param userId    Идентификатор пользователя.
     * @param startDate Начальная дата периода.
     * @param endDate   Конечная дата периода.
     * @return Сумма доходов пользователя за указанный период в виде {@link BigDecimal}.
     */
    BigDecimal getIncomeByUserIdByPeriodDate(Long userId, LocalDate startDate, LocalDate endDate);

    /**
     * Возвращает общую сумму доходов пользователя.
     *
     * @param userId Идентификатор пользователя.
     * @return Сумма доходов пользователя в виде {@link BigDecimal}.
     */
    BigDecimal getIncomeByUserId(Long userId);

    /**
     * Возвращает сумму расходов пользователя за текущий месяц.
     *
     * @param userId Идентификатор пользователя.
     * @return Сумма расходов пользователя за текущий месяц в виде {@link BigDecimal}.
     */
    BigDecimal getConsumptionByUserIdByMonth(Long userId);

    /**
     * Возвращает баланс пользователя (разницу между доходами и расходами).
     *
     * @param userId Идентификатор пользователя.
     * @return Баланс пользователя в виде {@link BigDecimal}.
     */
    BigDecimal getBalanceByUserId(Long userId);

    /**
     * Находит все транзакции пользователя по указанной дате.
     *
     * @param userId Идентификатор пользователя.
     * @param date   Дата, по которой выполняется поиск транзакций.
     * @return Список транзакций пользователя за указанную дату.
     */
    List<Transaction> findAllByUserIdByDate(Long userId, LocalDate date);

    /**
     * Находит все транзакции пользователя по указанной категории.
     *
     * @param userId   Идентификатор пользователя.
     * @param category Категория, по которой выполняется поиск транзакций.
     * @return Список транзакций пользователя по указанной категории.
     */
    List<Transaction> findAllByUserIdByCategory(Long userId, Category category);

    /**
     * Находит все транзакции пользователя по типу (доход/расход).
     *
     * @param userId Идентификатор пользователя.
     * @param income Флаг, указывающий тип транзакции: true — доход, false — расход.
     * @return Список транзакций пользователя по указанному типу.
     */
    List<Transaction> findAllByUserIdByIncome(Long userId, boolean income);

    /**
     * Возвращает сумму расходов пользователя по указанной категории.
     *
     * @param userId   Идентификатор пользователя.
     * @param category Категория, по которой выполняется расчёт расходов.
     * @return Сумма расходов пользователя по указанной категории в виде {@link BigDecimal}.
     */
    BigDecimal getConsumptionByUserIdByCategory(Long userId, Category category);
}
