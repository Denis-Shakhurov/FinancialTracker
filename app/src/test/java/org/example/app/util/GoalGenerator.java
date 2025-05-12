package org.example.app.util;

import net.datafaker.Faker;
import org.example.app.application.dto.goal.GoalCreateDTO;
import org.example.app.application.dto.goal.GoalDTO;
import org.example.app.application.dto.goal.GoalEditDTO;
import org.example.app.domain.model.Goal;
import org.instancio.Instancio;

import java.math.BigDecimal;

import static org.instancio.Select.field;


public class GoalGenerator {
    private final Faker faker = new Faker();

    public GoalDTO getGoalDTO() {
        return Instancio.of(GoalDTO.class)
                .set(field(GoalDTO::getId), faker.number().randomNumber())
                .set(field(GoalDTO::getUserId), faker.number().randomNumber())
                .set(field(GoalDTO::getDescription), faker.text().text(15))
                .set(field(GoalDTO::getTargetAmount),
                        BigDecimal.valueOf(
                                faker.number().randomDouble(2, 0, 1000)))
                .create();
    }

    public GoalCreateDTO getCreateDTO() {
        return Instancio.of(GoalCreateDTO.class)
                .set(field(GoalCreateDTO::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(GoalCreateDTO::getDescription), faker.text().text(15))
                .set(field(GoalCreateDTO::getTargetAmount),
                        BigDecimal.valueOf(
                                faker.number().randomDouble(2, 0, 1000)))
                .create();
    }

    public GoalEditDTO getEditDTO() {
        return Instancio.of(GoalEditDTO.class)
                .set(field(GoalEditDTO::getId), (long) faker.number().numberBetween(1, 200))
                .set(field(GoalEditDTO::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(GoalEditDTO::getDescription), faker.text().text(15))
                .set(field(GoalEditDTO::getTargetAmount),
                        BigDecimal.valueOf(
                                faker.number().randomDouble(2, 0, 1000)))
                .create();
    }

    public Goal getGoal() {
        return Instancio.of(Goal.class)
                .ignore(field(Goal::getId))
                .set(field(Goal::getUserId), (long) faker.number().numberBetween(1, 200))
                .set(field(Goal::getDescription), faker.text().text(15))
                .set(field(Goal::getTargetAmount),
                        BigDecimal.valueOf(
                                faker.number().randomDouble(2, 0, 1000)))
                .create();
    }
}
