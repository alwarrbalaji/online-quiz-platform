package com.quiz.dao;

import com.quiz.model.Feedback;
import com.quiz.util.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FeedbackDao {

    public boolean saveFeedback(Feedback feedback) {
        String sql = "INSERT INTO feedback (username, quiz_title, rating, comment, submitted_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, feedback.getUsername());
            ps.setString(2, feedback.getQuizTitle());
            ps.setInt(3, feedback.getRating());
            ps.setString(4, feedback.getComment());
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.parse(feedback.getSubmittedAt())));
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Feedback> getAllFeedback() {
        List<Feedback> list = new ArrayList<>();
        String sql = "SELECT * FROM feedback ORDER BY submitted_at DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Feedback f = new Feedback();
                f.setId(rs.getLong("id"));
                f.setUsername(rs.getString("username"));
                f.setQuizTitle(rs.getString("quiz_title"));
                f.setRating(rs.getInt("rating"));
                f.setComment(rs.getString("comment"));
                f.setSubmittedAt(rs.getTimestamp("submitted_at").toLocalDateTime().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                list.add(f);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
