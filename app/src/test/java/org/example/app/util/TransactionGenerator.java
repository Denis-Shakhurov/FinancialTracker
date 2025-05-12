package org.example.app.util;

import net.datafaker.Faker;
import org.example.app.application.dto.transaction.TransactionCreateDTO;
import org.example.app.application.dto.transaction.TransactionDTO;
import org.example.app.application.dto.transaction.TransactionEditDTO;
import org.example.app.domain.model.Category;
import org.example.app.domain.model.Transaction;
import org.instancio.Instancio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.instancio.Select.field;

public class TransactionGenerator {
    private final Faker faker = new Faker();

    public TransactionDTO getTransactionDTO() {
        return Instancio.of(TransactionDTO.class)
                .set(field(TransactionDTO::getId), faker.number().randomNumber())
                .set(field(TransactionDTO::getUserId), faker.number().randomNumber())
                .set(field(TransactionDTO::getDescription), faker.text().text(15))
                .set(field(TransactionDTO::getAmount),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 1, 1000)))
                .set(field(TransactionDTO::getDate), randomDate())
                .set(field(TransactionDTO::getCategory), faker.options().option(Category.values()))
                .set(field(TransactionDTO::isIncome), faker.bool().bool())
                .create();
    }

    public TransactionCreateDTO getCreateDTO() {
        return Instancio.of(TransactionCreateDTO.class)
                .set(field(TransactionCreateDTO::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(TransactionCreateDTO::getAmount),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 1, 1000)))
                .set(field(TransactionCreateDTO::getDate), randomDate())
                .set(field(TransactionCreateDTO::getDescription), faker.text().text(15))
                .set(field(TransactionCreateDTO::getCategory), faker.options().option(Category.values()))
                .set(field(TransactionCreateDTO::isIncome), faker.bool().bool())
                .create();
    }

    public TransactionEditDTO getEditDTO() {
        return Instancio.of(TransactionEditDTO.class)
                .set(field(TransactionEditDTO::getId), (long) faker.number().numberBetween(1, 200))
                .set(field(TransactionEditDTO::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(TransactionEditDTO::getDescription), faker.text().text(15))
                .set(field(TransactionEditDTO::getAmount),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 1, 1000)))
                .set(field(TransactionEditDTO::getDate), randomDate())
                .set(field(TransactionEditDTO::getCategory), faker.options().option(Category.values()))
                .set(field(TransactionEditDTO::isIncome), faker.bool().bool())
                .create();
    }

    public Transaction getTransaction() {
        return Instancio.of(Transaction.class)
                .ignore(field(Transaction::getId))
                .set(field(Transaction::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(Transaction::getDescription), faker.text().text(15))
                .set(field(Transaction::getAmount),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 1, 1000)))
                .set(field(Transaction::getDate), randomDate())
                .set(field(Transaction::getCategory), faker.options().option(Category.values()))
                .set(field(Transaction::isIncome), faker.bool().bool())
                .create();
    }

    public List<Transaction> getTransactionList() {
        List<Transaction> list = new ArrayList<>();
        list.add(new Transaction(
                null,
                1234L,
                new BigDecimal(1200),
                Category.PRODUCTS,
                "text",
                LocalDate.of(2025, 3, 5),
                false));
        list.add(new Transaction(
                null,
                1234L,
                new BigDecimal(100),
                Category.PRODUCTS,
                "milk",
                LocalDate.of(2025, 3, 7),
                false));
        list.add(new Transaction(
                null,
                1234L,
                new BigDecimal(1200),
                Category.HOUSE,
                "",
                LocalDate.of(2025, 3, 1),
                false));
        list.add(new Transaction(
                null,
                1234L,
                new BigDecimal(50000),
                Category.INCOME,
                "text",
                LocalDate.of(2025, 2, 25),
                true));

        return list;
    }

    private LocalDate randomDate() {
        return faker.date()
                .past(30, TimeUnit.DAYS)
                .toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
