package org.rssreader.models;

import java.time.LocalDateTime;
import java.util.Objects;

public class User {
    private final String username;
    private final String password;      // a sima jelszó, amit a DAO hash-el
    private String email;
    private LocalDateTime createdAt;    // DB-ből lekért regisztrációs idő

    // Regisztrációkor: a createdAt még null, DAO állítja be az INSERT után
    public User(String username, String password, String email) {
        this(username, password, email, null);
    }

    // AuthUser után: a createdAt-ot is betölti a DAO
    public User(String username, String password, String email, LocalDateTime createdAt) {
        this.username  = username;
        this.password  = password;
        this.email     = email;
        this.createdAt = createdAt;
    }

    public String getUsername() {
        return username;
    }

    /** A tiszta (nem hash-elt) jelszó, amit a DAO ellenőriz és hash-el */
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    /** A regisztráció időpontja (DB→DAO→modell) */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User u = (User) o;
        return Objects.equals(username, u.username)
                && Objects.equals(password, u.password)
                && Objects.equals(email,    u.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, email);
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email + "'}";
    }
}
