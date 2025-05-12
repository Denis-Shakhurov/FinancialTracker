package org.example.app.util;

import net.datafaker.Faker;
import org.example.app.application.dto.user.UserCreateDTO;
import org.example.app.application.dto.user.UserDTO;
import org.example.app.application.dto.user.UserEditDTO;
import org.example.app.domain.model.Role;
import org.example.app.domain.model.User;
import org.instancio.Instancio;

import static org.instancio.Select.field;

public class UserGenerator {
    private final Faker faker = new Faker();

    public UserDTO getUserDTO() {
        return Instancio.of(UserDTO.class)
                .set(field(UserDTO::getId), (long) faker.number().numberBetween(1, 200))
                .set(field(UserDTO::getName), faker.name().fullName())
                .set(field(UserDTO::getEmail), faker.internet().emailAddress())
                .set(field(UserDTO::getRole), faker.options().option(Role.values()))
                .set(field(UserDTO::isBanned), faker.bool().bool())
                .create();
    }

    public UserCreateDTO getCreateDTO() {
        return Instancio.of(UserCreateDTO.class)
                .set(field(UserCreateDTO::getName), faker.name().name())
                .set(field(UserCreateDTO::getEmail), faker.internet().emailAddress())
                .set(field(UserCreateDTO::getPassword), faker.internet().password())
                .set(field(UserCreateDTO::getRole), faker.options().option(Role.values()))
                .set(field(UserCreateDTO::isBanned), faker.bool().bool())
                .create();
    }

    public UserEditDTO getEditDTO() {
        return Instancio.of(UserEditDTO.class)
                .set(field(UserEditDTO::getId), (long) faker.number().numberBetween(1, 200))
                .set(field(UserEditDTO::getName), faker.name().fullName())
                .set(field(UserEditDTO::getEmail), faker.internet().emailAddress())
                .set(field(UserEditDTO::getRole), faker.options().option(Role.values()))
                .set(field(UserEditDTO::isBanned), faker.bool().bool())
                .create();
    }

    public User getUser() {
        return Instancio.of(User.class)
                .ignore(field(User::getId))
                .set(field(User::getName), faker.internet().username())
                .set(field(User::getEmail), faker.internet().emailAddress())
                .set(field(User::getPassword), faker.internet().password())
                .set(field(User::getRole), faker.options().option(Role.values()))
                .set(field(User::isBanned), faker.bool().bool())
                .create();
    }
}
