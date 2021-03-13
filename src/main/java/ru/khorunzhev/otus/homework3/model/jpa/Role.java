package ru.khorunzhev.otus.homework3.model.jpa;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;
import java.util.stream.Collectors;

import static ru.khorunzhev.otus.homework2.model.Permission.*;

public enum Role {
    USER(Set.of(BOOK_READ)),
    ADMIN(Set.of(BOOK_READ, BOOK_WRITE));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getAuthorities() {
        return getPermissions().stream()
                .map(permission -> {
                    return new SimpleGrantedAuthority(permission.getPermission());
                })
                .collect(Collectors.toSet());
    }
}
