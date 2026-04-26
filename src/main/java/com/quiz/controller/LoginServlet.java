package com.quiz.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.quiz.dao.RewardDao;
import com.quiz.dao.UserDao;
import com.quiz.model.User;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private UserDao   userDao;
    private RewardDao rewardDao;

    @Override
    public void init() {
        userDao   = new UserDao();
        rewardDao = new RewardDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // If already logged in go to dashboard
        HttpSession existing = request.getSession(false);
        if (existing != null && existing.getAttribute("currentUser") != null) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        // ← FIXED: login.jsp is inside jsp/ folder
        request.getRequestDispatcher("/jsp/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Validate inputs not empty
        if (isNullOrEmpty(username) || isNullOrEmpty(password)) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?error=2");
            return;
        }

        // Validate credentials
        User user = userDao.validateUser(username.trim(), password.trim());

        if (user != null) {
            // Session fixation fix
            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }

            // Create new session
            HttpSession session = request.getSession(true);
            session.setAttribute("currentUser", user);
            session.setMaxInactiveInterval(60 * 60); // 1 hour

            // Update login streak
            rewardDao.updateLoginStreak(user.getId());

            // ← FIXED: redirect to dashboard servlet
            response.sendRedirect(request.getContextPath() + "/dashboard");

        } else {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp?error=1");
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}