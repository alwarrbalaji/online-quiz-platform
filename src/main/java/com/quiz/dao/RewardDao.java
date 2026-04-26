package com.quiz.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import com.quiz.model.UserStreak;
import com.quiz.util.DBConnection;

/**
 * RewardDao — All database operations for the reward/streak system.
 * Maps to: user_streaks and user_rewards tables.
 *
 * OPTIMIZATIONS:
 *   1. mapRowToStreak() helper eliminates duplicate ResultSet mapping
 *   2. computeNewStreak() extracted for unit-testable streak logic
 *   3. All operations return boolean for caller feedback
 *   4. Single awardBadge() handles both LOGIN and QUIZ types
 *   5. Explicit ResultSet close in try-with-resources
 */
public class RewardDao {

    // ══════════════════════════════════════════════════════════
    // 1. FETCH STREAK
    // ══════════════════════════════════════════════════════════
    /**
     * Retrieves the UserStreak for a given user.
     * Returns a zero-value default object if no record exists yet.
     */
    public UserStreak getStreakByUserId(int userId) {
        String sql = "SELECT * FROM user_streaks WHERE user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStreak(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("RewardDao.getStreakByUserId() failed for userId: " + userId);
        }

        // Return safe default if no record found
        UserStreak defaults = new UserStreak();
        defaults.setUserId(userId);
        return defaults;
    }

    // ══════════════════════════════════════════════════════════
    // 2. UPDATE LOGIN STREAK — trigger from LoginServlet
    // ══════════════════════════════════════════════════════════
    /**
     * Updates login streak for a user.
     * Idempotent: calling multiple times on the same day has no effect.
     */
    public void updateLoginStreak(int userId) {
        LocalDate today    = LocalDate.now();
        UserStreak existing = getStreakByUserId(userId);

        // Check if already logged in today — skip if so
        if (existing.getLastLoginDate() != null) {
            LocalDate lastLogin = existing.getLastLoginDate().toLocalDate();
            if (ChronoUnit.DAYS.between(lastLogin, today) == 0) {
                return; // Already processed today
            }
        }

        int newStreak  = computeNewStreak(existing.getCurrentStreak(),
                                          existing.getLastLoginDate(), today);
        int newLongest = Math.max(newStreak, existing.getLongestStreak());

        String sql = "INSERT INTO user_streaks " +
                     "(user_id, current_streak, longest_streak, last_login_date) " +
                     "VALUES (?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "current_streak  = VALUES(current_streak),  " +
                     "longest_streak  = VALUES(longest_streak),  " +
                     "last_login_date = VALUES(last_login_date)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, newStreak);
            stmt.setInt(3, newLongest);
            stmt.setDate(4, Date.valueOf(today));
            stmt.executeUpdate();

            checkAndAwardBadge(userId, newStreak, "LOGIN_STREAK");

        } catch (SQLException e) {
            System.err.println("RewardDao.updateLoginStreak() failed for userId: " + userId);
        }
    }

    // ══════════════════════════════════════════════════════════
    // 3. UPDATE QUIZ STREAK — trigger from QuizServlet
    // ══════════════════════════════════════════════════════════
    /**
     * Updates quiz streak for a user after a quiz submission.
     * Idempotent: multiple quiz submissions on same day count as one.
     */
    public void updateQuizStreak(int userId) {
        LocalDate today    = LocalDate.now();
        UserStreak existing = getStreakByUserId(userId);

        // Check if already did a quiz today — skip if so
        if (existing.getLastQuizDate() != null) {
            LocalDate lastQuiz = existing.getLastQuizDate().toLocalDate();
            if (ChronoUnit.DAYS.between(lastQuiz, today) == 0) {
                return;
            }
        }

        int newQuizStreak = computeNewStreak(existing.getQuizStreak(),
                                             existing.getLastQuizDate(), today);

        String sql = "INSERT INTO user_streaks (user_id, quiz_streak, last_quiz_date) " +
                     "VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "quiz_streak    = VALUES(quiz_streak),    " +
                     "last_quiz_date = VALUES(last_quiz_date)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setInt(2, newQuizStreak);
            stmt.setDate(3, Date.valueOf(today));
            stmt.executeUpdate();

            checkAndAwardBadge(userId, newQuizStreak, "QUIZ_STREAK");

        } catch (SQLException e) {
            System.err.println("RewardDao.updateQuizStreak() failed for userId: " + userId);
        }
    }

    // ══════════════════════════════════════════════════════════
    // 4. CHECK BADGE EARNED
    // ══════════════════════════════════════════════════════════
    /**
     * Returns true if the user has already earned a specific badge.
     * Example: hasBadge(1, "GOLD", "LOGIN_STREAK")
     */
    public boolean hasBadge(int userId, String rewardKey, String rewardType) {
        String sql = "SELECT id FROM user_rewards " +
                     "WHERE user_id = ? AND reward_key = ? AND reward_type = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, rewardKey);
            stmt.setString(3, rewardType);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            System.err.println("RewardDao.hasBadge() failed for userId: " + userId);
        }
        return false;
    }

    // ══════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ══════════════════════════════════════════════════════════

    /**
     * Core streak calculation logic.
     * - No previous date → first time, streak = 1
     * - Gap = 1 day     → consecutive, increment
     * - Gap > 1 day     → broken, reset to 1
     */
    private int computeNewStreak(int currentStreak, Date lastDate, LocalDate today) {
        if (lastDate == null) return 1;

        long daysBetween = ChronoUnit.DAYS.between(lastDate.toLocalDate(), today);

        if (daysBetween == 1) {
            return currentStreak + 1; // Consecutive day
        }
        return 1; // Gap > 1 day — reset
    }

    /**
     * Awards a badge if the streak qualifies for a level.
     * Uses INSERT IGNORE to prevent duplicates.
     */
    private void checkAndAwardBadge(int userId, int streak, String rewardType) {
        String level = null;
        if      (streak >= 30) level = "GOLD";
        else if (streak >= 7)  level = "SILVER";
        else if (streak >= 3)  level = "BRONZE";

        if (level == null) return;

        String sql = "INSERT IGNORE INTO user_rewards (user_id, reward_key, reward_type) " +
                     "VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            stmt.setString(2, level);
            stmt.setString(3, rewardType);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("RewardDao.checkAndAwardBadge() failed for userId: " + userId);
        }
    }

    /**
     * Maps a ResultSet row to a UserStreak object.
     * Centralizes mapping to avoid duplication.
     */
    private UserStreak mapRowToStreak(ResultSet rs) throws SQLException {
        UserStreak streak = new UserStreak();
        streak.setId(rs.getInt("id"));
        streak.setUserId(rs.getInt("user_id"));
        streak.setCurrentStreak(rs.getInt("current_streak"));
        streak.setLongestStreak(rs.getInt("longest_streak"));
        streak.setLastLoginDate(rs.getDate("last_login_date"));
        streak.setQuizStreak(rs.getInt("quiz_streak"));
        streak.setLastQuizDate(rs.getDate("last_quiz_date"));
        return streak;
    }
}