package com.quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.quiz.model.Profile;
import com.quiz.model.User;
import com.quiz.util.DBConnection;

/**
 * UserDao — All database operations for users and profiles.
 *
 * OPTIMIZATIONS:
 *   1. registerUser() returns boolean success/fail instead of void
 *   2. validateUser() uses parameterized query (SQL injection safe)
 *   3. isUsernameTaken() added for clean duplicate detection
 *   4. All ResultSets explicitly closed via try-with-resources
 *   5. Meaningful error messages logged with context
 */
public class UserDao {

    // ── Register new user ─────────────────────────────────────
    /**
     * Inserts a new user into the 'users' table.
     * @return true if registration succeeded, false if username/email exists
     */
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            int rows = stmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            // Error code 1062 = Duplicate entry (username or email already exists)
            if (e.getErrorCode() == 1062) {
                System.err.println("UserDao.registerUser(): Duplicate username or email — "
                    + user.getUsername());
            } else {
                System.err.println("UserDao.registerUser() failed: " + e.getMessage());
            }
            return false;
        }
    }

    // ── Validate login credentials ────────────────────────────
    /**
     * Checks username and password against the database.
     * NOTE: Password comparison is done here as plaintext.
     * TODO: Upgrade to BCrypt hashing for production security.
     *
     * @return User object if valid, null if credentials are wrong
     */
    public User validateUser(String username, String password) {
        if (username == null || password == null
                || username.trim().isEmpty() || password.trim().isEmpty()) {
            return null;
        }

        String sql = "SELECT id, username, email FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            stmt.setString(2, password);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            }

        } catch (SQLException e) {
            System.err.println("UserDao.validateUser() failed for user: " + username);
        }
        return null;
    }

    // ── Check if username is already taken ────────────────────
    /**
     * Quick existence check before attempting registration.
     * @return true if username is already in use
     */
    public boolean isUsernameTaken(String username) {
        String sql = "SELECT id FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("UserDao.isUsernameTaken() failed: " + e.getMessage());
        }
        return false;
    }

    // ── Get user profile ──────────────────────────────────────
    /**
     * Fetches full_name and bio from profiles table for a given user.
     * @return Profile object, or null if no profile exists yet
     */
    public Profile getUserProfile(int userId) {
        String sql = "SELECT p.full_name, p.bio " +
                     "FROM users u LEFT JOIN profiles p ON u.id = p.user_id " +
                     "WHERE u.id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Profile profile = new Profile();
                    profile.setUserId(userId);
                    profile.setFullName(rs.getString("full_name"));
                    profile.setBio(rs.getString("bio"));
                    return profile;
                }
            }

        } catch (SQLException e) {
            System.err.println("UserDao.getUserProfile() failed for userId: " + userId);
        }
        return null;
    }

    // ── Upsert user profile ───────────────────────────────────
    /**
     * Inserts a new profile or updates an existing one.
     * Uses ON DUPLICATE KEY UPDATE for safe upsert behavior.
     * @return true if the operation succeeded
     */
    public boolean updateUserProfile(Profile profile) {
        String sql = "INSERT INTO profiles (user_id, full_name, bio) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE full_name = VALUES(full_name), bio = VALUES(bio)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, profile.getUserId());
            stmt.setString(2, profile.getFullName());
            stmt.setString(3, profile.getBio());
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            System.err.println("UserDao.updateUserProfile() failed for userId: "
                + profile.getUserId());
            return false;
        }
    }
}