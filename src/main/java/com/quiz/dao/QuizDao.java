package com.quiz.dao;

import com.quiz.model.Quiz;
import com.quiz.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDao {

    // This method gets all quizzes from the 'quizzes' table
	public List<Quiz> getAllQuizzes() {
	    List<Quiz> quizzes = new ArrayList<>();
	    String sql = "SELECT * FROM quizzes";
	    try (Connection conn = DBConnection.getConnection();
	         Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(sql)) {

	        while (rs.next()) {
	            Quiz quiz = new Quiz();
	            quiz.setId(rs.getInt("id"));
	            quiz.setSubject(rs.getString("subject"));
	            
	            // This is the updated part
	            String category = rs.getString("category");
	            if (category == null || category.trim().isEmpty()) {
	                quiz.setCategory("General"); // Assign a default category
	            } else {
	                quiz.setCategory(category);
	            }
	            
	            quizzes.add(quiz);
	        }
	    } catch (SQLException e) {
	        System.err.println("QuizDao.getAllQuizzes() failed: " + e.getMessage());
	    }
	    return quizzes;
	}

	public Quiz getQuizById(int id) {
	    String sql = "SELECT * FROM quizzes WHERE id = ?";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setInt(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                Quiz quiz = new Quiz();
	                quiz.setId(rs.getInt("id"));
	                quiz.setSubject(rs.getString("subject"));
	                quiz.setCategory(rs.getString("category"));
	                return quiz;
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("QuizDao.getQuizById() failed: " + e.getMessage());
	    }
	    return null;
	}
}