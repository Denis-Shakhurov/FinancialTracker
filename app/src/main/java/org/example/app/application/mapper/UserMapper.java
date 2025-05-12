package org.example.app.application.mapper;

import org.example.app.application.dto.user.UserCreateDTO;
import org.example.app.application.dto.user.UserDTO;
import org.example.app.application.dto.user.UserEditDTO;
import org.example.app.domain.model.User;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Mapper for converting between User DTOs and entities.
 * Provides mapping functionality for User, UserCreateDTO, UserDTO and UserEditDTO.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public abstract class UserMapper {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    /**
     * Maps User entity to UserDTO.
     *
     * @param user the User entity to convert
     * @return converted UserDTO
     */
    @Mapping(target = "name", source = "name")
    @Mapping(target = "email", source = "email")
    public abstract UserDTO map(User user);

    /**
     * Maps UserCreateDTO to User entity.
     * Encodes the password using password encoder.
     *
     * @param dto the UserCreateDTO to convert
     * @return converted User entity
     */
    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "role", source = "dto.role")
    @Mapping(target = "banned", source = "dto.banned")
    @Mapping(target = "id", ignore = true)
    public abstract User map(UserCreateDTO dto);

    /**
     * Updates existing User entity with data from UserEditDTO.
     * Encodes the password if it's present in the DTO.
     *
     * @param dto the UserEditDTO containing updated data
     * @param user the User entity to be updated
     */
    @Mapping(target = "password", source = "password", qualifiedByName = "encodePassword")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "email", source = "dto.email")
    @Mapping(target = "role", source = "dto.role")
    @Mapping(target = "banned", source = "dto.banned")
    @Mapping(target = "id", source = "dto.id")
    public abstract void update(UserEditDTO dto, @MappingTarget User user);

    /**
     * Encodes raw password using password encoder.
     *
     * @param rawPassword the raw password to encode
     * @return encoded password
     * @throws IllegalArgumentException if raw password is null or empty
     * @see PasswordEncoder#encode(CharSequence)
     */
    @Named("encodePassword")
    protected String encodePassword(String rawPassword) {
        if (rawPassword == null || rawPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        return passwordEncoder.encode(rawPassword);
    }
}