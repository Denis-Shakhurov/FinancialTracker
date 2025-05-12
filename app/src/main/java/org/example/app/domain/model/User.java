package org.example.app.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Класс, представляющий пользователя системы.
 * Содержит информацию о пользователе, такую как идентификатор, имя, email, пароль, роль и статус блокировки.
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    /**
     * Уникальный идентификатор пользователя.
     */
    private Long id;

    /**
     * Имя пользователя. Содержит полное имя или псевдоним пользователя.
     */
    private String name;

    /**
     * Электронная почта пользователя. Используется для идентификации и связи с пользователем.
     */
    private String email;

    /**
     * Пароль пользователя. Хранится в зашифрованном виде для обеспечения безопасности.
     */
    private String password;

    /**
     * Роль пользователя. Определяет уровень доступа и права пользователя в системе.
     */
    private Role role;

    /**
     * Флаг, указывающий, заблокирован ли пользователь. Если true, пользователь не может войти в систему.
     */
    private boolean banned;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.getAuthority()));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
