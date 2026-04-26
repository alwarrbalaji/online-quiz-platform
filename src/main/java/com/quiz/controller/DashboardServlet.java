package com.quiz.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.quiz.dao.CourseDao;
import com.quiz.dao.QuizDao;
import com.quiz.dao.RewardDao;
import com.quiz.model.Course;
import com.quiz.model.Quiz;
import com.quiz.model.User;
import com.quiz.model.UserStreak;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private QuizDao   quizDao;
    private RewardDao rewardDao;
    private CourseDao courseDao;

    @Override
    public void init() {
        quizDao   = new QuizDao();
        rewardDao = new RewardDao();
        courseDao = new CourseDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Session guard
        HttpSession session = request.getSession(false);
        User currentUser = (session != null)
            ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        request.setAttribute("currentUser", currentUser);

        // Load user's private generated courses
        List<Course> userCourses = courseDao.getCoursesByUserId(currentUser.getId());
        request.setAttribute("userCourses", userCourses);

        // Load pre-made quizzes grouped by category (excluding AI generated ones for privacy)
        List<Quiz> allQuizzes = quizDao.getAllQuizzes();
        Map<String, List<Quiz>> quizzesByCategory = new LinkedHashMap<>();
        for (Quiz quiz : allQuizzes) {
            String cat = (quiz.getCategory() != null && !quiz.getCategory().trim().isEmpty())
                ? quiz.getCategory() : "General";
            
            // Only add to global dashboard list if it's NOT an AI-generated course quiz
            if (!"Generated".equalsIgnoreCase(cat)) {
                quizzesByCategory
                    .computeIfAbsent(cat, k -> new ArrayList<>())
                    .add(quiz);
            }
        }
        request.setAttribute("quizzesByCategory", quizzesByCategory);

        // Load streak for dashboard widget
        UserStreak streak = rewardDao.getStreakByUserId(currentUser.getId());
        request.setAttribute("streak", streak);

        // ← FIXED: dashboard.jsp is inside jsp/ folder
        request.getRequestDispatcher("/jsp/dashboard.jsp")
               .forward(request, response);
    }
}