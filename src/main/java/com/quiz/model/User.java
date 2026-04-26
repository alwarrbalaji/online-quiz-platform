package com.quiz.model;

/**
 * User — Core authentication POJO.
 * Maps to the 'users' table.
 *
 * OPTIMIZATIONS:
 *   1. Added toString() for easy debug logging
 *   2. Added equals() and hashCode() for safe session comparisons
 *   3. Input sanitization in setters (trim on strings)
 */
public class User {

    private int    id;
    private String username;
    private String password;
    private String email;

    // ── Constructors ──────────────────────────────────────────
    public User() {}

    public User(int id, String username, String email) {
        this.id       = id;
        this.username = username;
        this.email    = email;
    }

    // ── Getters & Setters ─────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = (username != null) ? username.trim() : null;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) {
        this.email = (email != null) ? email.trim().toLowerCase() : null;
    }

    // ── Utility ───────────────────────────────────────────────
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', email='" + email + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User other = (User) o;
        return this.id == other.id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}