package com.quiz.dao;

import com.quiz.model.Result;
import com.quiz.model.PerformanceStats;
import com.quiz.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ResultDao {

    /**
     * Saves a user's quiz result to the database, including the total number of questions.
     * @param userId The ID of the user who took the quiz.
     * @param quizId The ID of the quiz that was taken.
     * @param score The number of correct answers.
     * @param totalQuestions The total number of questions in the quiz.
     */
    public void saveResult(int userId, int quizId, int score, int totalQuestions) {
        String sql = "INSERT INTO results (user_id, quiz_id, score, total_questions) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, quizId);
            stmt.setInt(3, score);
            stmt.setInt(4, totalQuestions);
            stmt.executeUpdate();
        } catch (Exception e) {
        }
    }

    /**
     * Retrieves a list of all past results for a specific user, ordered by the most recent.
     * @param userId The ID of the user whose results are to be fetched.
     * @return A list of Result objects.
     */
    public List<Result> getResultsByUserId(int userId) {
        List<Result> results = new ArrayList<>();
        String sql = "SELECT q.subject, r.score, r.total_questions, r.date_taken " +
                     "FROM results r JOIN quizzes q ON r.quiz_id = q.id " +
                     "WHERE r.user_id = ? ORDER BY r.date_taken DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Result result = new Result();
                result.setQuizSubject(rs.getString("subject"));
                result.setScore(rs.getInt("score"));
                result.setTotalQuestions(rs.getInt("total_questions"));
                result.setDateTaken(rs.getTimestamp("date_taken"));
                results.add(result);
            }
        } catch (SQLException e) {
        }
        return results;
    }
 // In com.quiz.dao.ResultDao.java

    public PerformanceStats getUserPerformanceStats(int userId) {
        PerformanceStats stats = new PerformanceStats();
        // This query calculates the total quizzes taken, sum of scores, and sum of total questions
        String sql = "SELECT COUNT(*) as quizzesTaken, SUM(score) as totalCorrect, SUM(total_questions) as totalAnswered " +
                     "FROM results WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int quizzesTaken = rs.getInt("quizzesTaken");
                int totalCorrect = rs.getInt("totalCorrect");
                int totalAnswered = rs.getInt("totalAnswered");

                stats.setQuizzesTaken(quizzesTaken);
                stats.setTotalCorrectAnswers(totalCorrect);
                stats.setTotalQuestionsAnswered(totalAnswered);

                if (totalAnswered > 0) {
                    // Calculate the average score as a percentage
                    double average = ((double) totalCorrect / totalAnswered) * 100;
                    stats.setAverageScorePercentage(average);
                } else {
                    stats.setAverageScorePercentage(0);
                }
            }
        } catch (SQLException e) {
        }
        return stats;
    }
}

