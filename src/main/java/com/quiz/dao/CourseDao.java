package com.quiz.dao;

import com.quiz.model.*;
import com.quiz.util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CourseDao {

    /**
     * Saves a complete, AI-generated course and its associated quiz to the database.
     * This method uses a transaction to ensure all parts are saved successfully or none are.
     * @param course The main Course object.
     * @param lessons A list of Lesson objects for the course.
     * @param quiz The Quiz object associated with the course.
     * @param questions A list of Question objects for the quiz.
     * @param userId The ID of the user who created the course.
     * @return The ID of the newly created course, or 0 on failure.
     */
    public int saveGeneratedCourse(Course course, List<Lesson> lessons, Quiz quiz, List<Question> questions, int userId) {
        Connection conn = null;
        int courseId = 0;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Save the Course and get its new ID
            String courseSql = "INSERT INTO courses (title, introduction, created_by_user_id) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(courseSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, course.getTitle());
                stmt.setString(2, course.getIntroduction());
                stmt.setInt(3, userId);
                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        courseId = generatedKeys.getInt(1);
                    }
                }
            }

            // 2. Save the Lessons using the new course ID
            String lessonSql = "INSERT INTO lessons (course_id, title, material) VALUES (?, ?, ?)";
            for (Lesson lesson : lessons) {
                try (PreparedStatement stmt = conn.prepareStatement(lessonSql)) {
                    stmt.setInt(1, courseId);
                    stmt.setString(2, lesson.getTitle());
                    stmt.setString(3, lesson.getMaterial());
                    stmt.executeUpdate();
                }
            }

            // 3. Save the associated Quiz and get its new ID
            String quizSql = "INSERT INTO quizzes (subject, category) VALUES (?, ?)";
            int quizId = 0;
            try (PreparedStatement stmt = conn.prepareStatement(quizSql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, quiz.getSubject());
                stmt.setString(2, "Generated"); // Assign a category for AI-generated quizzes
                stmt.executeUpdate();
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        quizId = generatedKeys.getInt(1);
                    }
                }
            }
            
            // 4. Save the Questions and Options for the Quiz
            String questionSql = "INSERT INTO questions (quiz_id, question_text, difficulty) VALUES (?, ?, ?)";
            String optionSql = "INSERT INTO options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
            for (Question question : questions) {
                int questionId = 0;
                try (PreparedStatement stmt = conn.prepareStatement(questionSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, quizId);
                    stmt.setString(2, question.getQuestionText());
                    stmt.setString(3, "Medium"); // Assign a default difficulty
                    stmt.executeUpdate();
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            questionId = generatedKeys.getInt(1);
                        }
                    }
                }
                for (Option option : question.getOptions()) {
                    try (PreparedStatement stmt = conn.prepareStatement(optionSql)) {
                        stmt.setInt(1, questionId);
                        stmt.setString(2, option.getOptionText());
                        stmt.setBoolean(3, option.isCorrect());
                        stmt.executeUpdate();
                    }
                }
            }

            conn.commit(); // Commit the transaction if all steps succeed

        } catch (SQLException e) {
            System.err.println("Transaction is being rolled back.");
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { }
            return 0; // Return 0 to indicate failure
        } finally {
        }
        return courseId;
    }

    /**
     * Retrieves all courses created by a specific user.
     * @param userId The ID of the user whose courses are to be fetched.
     * @return A list of Course objects.
     */
    public List<Course> getCoursesByUserId(int userId) {
        List<Course> courses = new ArrayList<>();
        // This SQL query now has a WHERE clause to filter by the user ID
        String sql = "SELECT * FROM courses WHERE created_by_user_id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Course course = new Course();
                course.setId(rs.getInt("id"));
                course.setTitle(rs.getString("title"));
                course.setIntroduction(rs.getString("introduction"));
                // We don't need the lessons for the dashboard view
                courses.add(course);
            }
        } catch (SQLException e) {
        }
        return courses;
    }
    /**
     * Retrieves a single course and its associated lessons from the database.
     * @param courseId The ID of the course to fetch.
     * @return A Course object, complete with its list of lessons.
     */
    public Course getCourseById(int courseId) {
        Course course = null;
        String courseSql = "SELECT * FROM courses WHERE id = ?";
        String lessonSql = "SELECT * FROM lessons WHERE course_id = ?";
        String quizSql = "SELECT id FROM quizzes WHERE subject = ?";
        try (Connection conn = DBConnection.getConnection()) {
            // Get course details
            try (PreparedStatement stmt = conn.prepareStatement(courseSql)) {
                stmt.setInt(1, courseId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    course = new Course();
                    course.setId(rs.getInt("id"));
                    course.setTitle(rs.getString("title"));
                    course.setIntroduction(rs.getString("introduction"));
                }
            }
            try (PreparedStatement stmt = conn.prepareStatement(quizSql)) {
                stmt.setString(1, course.getTitle()); // It uses the course title to find the quiz
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    course.setQuizId(rs.getInt("id"));
                }
            }
            
            // If course exists, get its lessons
            if (course != null) {
                List<Lesson> lessons = new ArrayList<>();
                try (PreparedStatement stmt = conn.prepareStatement(lessonSql)) {
                    stmt.setInt(1, courseId);
                    ResultSet rs = stmt.executeQuery();
                    while (rs.next()) {
                        Lesson lesson = new Lesson();
                        lesson.setId(rs.getInt("id"));
                        lesson.setCourseId(rs.getInt("course_id"));
                        lesson.setTitle(rs.getString("title"));
                        lesson.setMaterial(rs.getString("material"));
                        lessons.add(lesson);
                    }
                    course.setLessons(lessons); // Assumes you have a List<Lesson> field in your Course model
                }
            }
        } catch (SQLException e) {
        }
        return course;
    }
}