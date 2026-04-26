package com.quiz.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.quiz.dao.RewardDao;
import com.quiz.model.User;
import com.quiz.model.UserStreak;

@WebServlet("/rewards")
public class RewardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RewardDao rewardDao;

    @Override
    public void init() {
        rewardDao = new RewardDao();
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

        int userId = currentUser.getId();

        // Fetch streak data
        UserStreak streak = rewardDao.getStreakByUserId(userId);

        // Fetch badge status
        boolean loginBronzeEarned = rewardDao.hasBadge(userId, "BRONZE", "LOGIN_STREAK");
        boolean loginSilverEarned = rewardDao.hasBadge(userId, "SILVER", "LOGIN_STREAK");
        boolean loginGoldEarned   = rewardDao.hasBadge(userId, "GOLD",   "LOGIN_STREAK");
        boolean quizBronzeEarned  = rewardDao.hasBadge(userId, "BRONZE", "QUIZ_STREAK");
        boolean quizSilverEarned  = rewardDao.hasBadge(userId, "SILVER", "QUIZ_STREAK");
        boolean quizGoldEarned    = rewardDao.hasBadge(userId, "GOLD",   "QUIZ_STREAK");

        // Set attributes
        request.setAttribute("streak",            streak);
        request.setAttribute("loginBronzeEarned", loginBronzeEarned);
        request.setAttribute("loginSilverEarned", loginSilverEarned);
        request.setAttribute("loginGoldEarned",   loginGoldEarned);
        request.setAttribute("quizBronzeEarned",  quizBronzeEarned);
        request.setAttribute("quizSilverEarned",  quizSilverEarned);
        request.setAttribute("quizGoldEarned",    quizGoldEarned);

        // ← FIXED: rewards.jsp is inside jsp/ folder
        request.getRequestDispatcher("/jsp/rewards.jsp")
               .forward(request, response);
    }
}