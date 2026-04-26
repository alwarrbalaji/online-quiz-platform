package com.quiz.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.quiz.model.Option;
import com.quiz.model.Question;
import com.quiz.util.DBConnection;

public class QuestionDao {
    // Fetches all questions for a specific quiz
    public List<Question> getQuestionsByQuizId(int quizId) {
        List<Question> questions = new ArrayList<>();
        String questionSql = "SELECT * FROM questions WHERE quiz_id = ?";
        String optionSql = "SELECT * FROM options WHERE question_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // First, get all questions for the quiz
            try (PreparedStatement questionStmt = conn.prepareStatement(questionSql)) {
                questionStmt.setInt(1, quizId);
                ResultSet rs = questionStmt.executeQuery();
                
                while (rs.next()) {
                    Question q = new Question();
                    q.setId(rs.getInt("id"));
                    q.setQuestionText(rs.getString("question_text"));
                    questions.add(q);
                }
            }
            
            // Then, for each question, get its options
            for (Question q : questions) {
                try (PreparedStatement optionStmt = conn.prepareStatement(optionSql)) {
                    optionStmt.setInt(1, q.getId());
                    ResultSet optionRs = optionStmt.executeQuery();
                    List<Option> options = new ArrayList<>();
                    while (optionRs.next()) {
                        Option o = new Option();
                        o.setId(optionRs.getInt("id"));
                        o.setOptionText(optionRs.getString("option_text"));
                        o.setCorrect(optionRs.getBoolean("is_correct"));
                        options.add(o);
                    }
                    q.setOptions(options);
                }
            }
        } catch (Exception e) {
        }
        return questions;
    }
}