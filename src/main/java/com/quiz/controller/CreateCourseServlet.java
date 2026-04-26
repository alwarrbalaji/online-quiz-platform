package com.quiz.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;

import com.quiz.dao.CourseDao;
import com.quiz.model.Course;
import com.quiz.model.Lesson;
import com.quiz.model.Option;
import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.User;
import com.quiz.service.GenAIService;

@WebServlet("/createCourse")
public class CreateCourseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private GenAIService genAIService = new GenAIService(); 
    private CourseDao courseDao = new CourseDao();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;
        
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        String topic = request.getParameter("topic");
        String level = request.getParameter("level");

        // 1. Call AI Service
        String generatedJson = genAIService.generateCourseContent(topic, level);

        // Check for empty AI response
        if (generatedJson != null && !generatedJson.trim().isEmpty()) {
            System.out.println("AI Response Received. Attempting to parse for User ID: " + currentUser.getId());
            
            int newCourseId = parseAndSave(generatedJson, currentUser.getId());
            
            if (newCourseId > 0) {
                response.sendRedirect(request.getContextPath() + "/viewCourse?id=" + newCourseId);
                return;
            }
        }
        
        // If it fails, send an error message to dashboard
        request.setAttribute("errorMessage", "AI failed to generate course. Please try a different topic.");
        request.getRequestDispatcher("/jsp/dashboard.jsp").forward(request, response);
    }

    private int parseAndSave(String json, int userId) {
        try {
            // Remove markdown backticks if AI included them
            String cleanedJson = json.replaceAll("```json", "").replaceAll("```", "").trim();
            JSONObject root = new JSONObject(cleanedJson);
            
            // 1. Course Metadata (Using optString to prevent crashes)
            Course course = new Course();
            course.setTitle(root.optString("courseTitle", root.optString("title", "Untitled Course")));
            course.setIntroduction(root.optString("introduction", "No introduction provided."));

            // 2. Lessons Parsing (Null-safe check)
            List<Lesson> lessons = new ArrayList<>();
            if (root.has("lessons")) {
                JSONArray lessonsJson = root.getJSONArray("lessons");
                for (int i = 0; i < lessonsJson.length(); i++) {
                    JSONObject lessonJson = lessonsJson.getJSONObject(i);
                    Lesson lesson = new Lesson();
                    lesson.setTitle(lessonJson.optString("lessonTitle", "Lesson " + (i + 1)));
                    lesson.setMaterial(lessonJson.optString("material", lessonJson.optString("content", "")));
                    lessons.add(lesson);
                }
            }

            // 3. Quiz Parsing (Null-safe check)
            Quiz quiz = new Quiz();
            quiz.setSubject(course.getTitle()); 
            List<Question> questions = new ArrayList<>();
            
            if (root.has("quiz")) {
                JSONArray quizJson = root.getJSONArray("quiz");
                for (int i = 0; i < quizJson.length(); i++) {
                    JSONObject qJson = quizJson.getJSONObject(i);
                    Question question = new Question();
                    question.setQuestionText(qJson.optString("questionText", "Question missing text."));
                    
                    List<Option> options = new ArrayList<>();
                    if (qJson.has("options")) {
                        JSONArray optsJson = qJson.getJSONArray("options");
                        for (int j = 0; j < optsJson.length(); j++) {
                            JSONObject oJson = optsJson.getJSONObject(j);
                            Option option = new Option();
                            option.setOptionText(oJson.optString("optionText", ""));
                            option.setCorrect(oJson.optBoolean("isCorrect", false));
                            options.add(option);
                        }
                    }
                    question.setOptions(options);
                    questions.add(question);
                }
            }

            // 4. Persistence
            return courseDao.saveGeneratedCourse(course, lessons, quiz, questions, userId);
            
        } catch (Exception e) {
            System.err.println("Critical Parsing Error: " + e.getMessage());
            return 0;
        }
    }
}