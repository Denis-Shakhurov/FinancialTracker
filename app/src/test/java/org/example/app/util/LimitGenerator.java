package org.example.app.util;

import net.datafaker.Faker;
import org.example.app.application.dto.limit.SpendingLimitCreateDTO;
import org.example.app.application.dto.limit.SpendingLimitDTO;
import org.example.app.application.dto.limit.SpendingLimitEditDTO;
import org.example.app.domain.model.SpendingLimit;
import org.instancio.Instancio;

import java.math.BigDecimal;

import static org.instancio.Select.field;

public class LimitGenerator {
    private final Faker faker = new Faker();

    public SpendingLimitDTO getSpendingLimitDTO() {
        return Instancio.of(SpendingLimitDTO.class)
                .set(field(SpendingLimitDTO::getId), faker.number().randomNumber())
                .set(field(SpendingLimitDTO::getLimit),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 0, 1000)))
                .set(field(SpendingLimitDTO::getUserId), faker.number().randomNumber())
                .set(field(SpendingLimitDTO::isActive), faker.bool().bool())
                .create();
    }

    public SpendingLimitCreateDTO getCreateDTO() {
        return Instancio.of(SpendingLimitCreateDTO.class)
                .set(field(SpendingLimitCreateDTO::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(SpendingLimitCreateDTO::isActive), faker.bool().bool())
                .set(field(SpendingLimitCreateDTO::getLimit),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 0, 1000)))
                .create();
    }

    public SpendingLimitEditDTO getEditDTO() {
        return Instancio.of(SpendingLimitEditDTO.class)
                .set(field(SpendingLimitEditDTO::getId), (long) faker.number().numberBetween(1, 200))
                .set(field(SpendingLimitEditDTO::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(SpendingLimitEditDTO::isActive), faker.bool().bool())
                .set(field(SpendingLimitEditDTO::getLimit),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 0, 1000)))
                .create();
    }

    public SpendingLimit getLimit() {
        return Instancio.of(SpendingLimit.class)
                .ignore(field(SpendingLimit::getId))
                .set(field(SpendingLimit::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(SpendingLimit::isActive), faker.bool().bool())
                .set(field(SpendingLimit::getLimit),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 0, 1000)))
                .create();

    }
}
