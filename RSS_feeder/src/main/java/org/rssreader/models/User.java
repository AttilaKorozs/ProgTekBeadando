package org.rssreader.models;

import java.time.LocalDateTime;

public class User {
    private final String username;
    private final String passwordHash;
    private final String email;
    private final LocalDateTime createdAt;

    public User(String username, String passwordHash, String email, LocalDateTime createdAt) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return username;
    }
}

