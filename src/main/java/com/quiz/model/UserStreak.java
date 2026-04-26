package com.quiz.model;

import java.sql.Date;

/**
 * UserStreak — Tracks login and quiz streaks per user.
 * Maps to the 'user_streaks' table.
 *
 * OPTIMIZATIONS:
 *   1. Streak level computation centralized in one private method
 *   2. Progress percentage helpers added for JSP progress bars
 *   3. Defensive defaults — never returns null from helpers
 */
public class UserStreak {

    private int    id;
    private int    userId;
    private int    currentStreak;
    private int    longestStreak;
    private Date   lastLoginDate;
    private int    quizStreak;
    private Date   lastQuizDate;

    // ── Constructors ──────────────────────────────────────────
    public UserStreak() {}

    // ── Getters & Setters ─────────────────────────────────────
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = Math.max(0, currentStreak);
    }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) {
        this.longestStreak = Math.max(0, longestStreak);
    }

    public Date getLastLoginDate() { return lastLoginDate; }
    public void setLastLoginDate(Date lastLoginDate) { this.lastLoginDate = lastLoginDate; }

    public int getQuizStreak() { return quizStreak; }
    public void setQuizStreak(int quizStreak) {
        this.quizStreak = Math.max(0, quizStreak);
    }

    public Date getLastQuizDate() { return lastQuizDate; }
    public void setLastQuizDate(Date lastQuizDate) { this.lastQuizDate = lastQuizDate; }

    // ── Level Computation ─────────────────────────────────────
    private String computeLevel(int streak) {
        if (streak >= 30) return "GOLD";
        if (streak >= 7)  return "SILVER";
        if (streak >= 3)  return "BRONZE";
        return "NONE";
    }

    public String getLoginLevel() { return computeLevel(currentStreak); }
    public String getQuizLevel()  { return computeLevel(quizStreak); }

    // ── Days to Next Level ────────────────────────────────────
    private int daysToNext(int streak) {
        if (streak < 3)  return 3  - streak;
        if (streak < 7)  return 7  - streak;
        if (streak < 30) return 30 - streak;
        return 0;
    }

    public int getDaysToNextLoginLevel() { return daysToNext(currentStreak); }
    public int getDaysToNextQuizLevel()  { return daysToNext(quizStreak); }

    // ── Progress Percentage (0–100) for progress bars ─────────
    private int progressPercent(int streak, int target) {
        if (streak >= target) return 100;
        return (int) ((streak / (double) target) * 100);
    }

    public int getBronzeLoginProgress() { return progressPercent(currentStreak, 3); }
    public int getSilverLoginProgress() { return progressPercent(currentStreak, 7); }
    public int getGoldLoginProgress()   { return progressPercent(currentStreak, 30); }

    public int getBronzeQuizProgress()  { return progressPercent(quizStreak, 3); }
    public int getSilverQuizProgress()  { return progressPercent(quizStreak, 7); }
    public int getGoldQuizProgress()    { return progressPercent(quizStreak, 30); }

    // ── toString ──────────────────────────────────────────────
    @Override
    public String toString() {
        return "UserStreak{userId=" + userId
            + ", loginStreak=" + currentStreak
            + ", quizStreak=" + quizStreak
            + ", loginLevel=" + getLoginLevel()
            + ", quizLevel=" + getQuizLevel() + "}";
    }
}