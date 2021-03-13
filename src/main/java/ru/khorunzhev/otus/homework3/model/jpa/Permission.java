package ru.khorunzhev.otus.homework3.model.jpa;

public enum Permission {
    BOOK_READ("book:read"),
    BOOK_WRITE("book:write");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
