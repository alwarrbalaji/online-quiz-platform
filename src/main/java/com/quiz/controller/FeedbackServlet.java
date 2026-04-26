package com.quiz.controller;

import com.quiz.dao.FeedbackDao;
import com.quiz.model.Feedback;
import com.quiz.model.User;
import com.google.gson.Gson;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/feedback/*")
public class FeedbackServlet extends HttpServlet {

    private final FeedbackDao feedbackDao = new FeedbackDao();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || !pathInfo.equals("/submit")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 1. Authenticate user
        HttpSession session = req.getSession(false);
        String username = "Anonymous";
        if (session != null) {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser != null) {
                username = currentUser.getUsername();
            }
        }

        // 2. Parse JSON body
        String jsonBody = req.getReader().lines().collect(Collectors.joining());
        Feedback feedback = gson.fromJson(jsonBody, Feedback.class);
        
        if (feedback == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        feedback.setUsername(username);
        feedback.setSubmittedAt(LocalDateTime.now().toString());

        // 3. Save feedback
        boolean success = feedbackDao.saveFeedback(feedback);
        
        resp.setContentType("application/json");
        if (success) {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write("{\"status\":\"success\"}");
        } else {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"status\":\"error\", \"message\":\"Failed to save feedback\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || !pathInfo.equals("/view-all")) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 1. Authenticate admin
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("adminUser") == null) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Admin access required");
            return;
        }

        // 2. Fetch all feedback
        List<Feedback> feedbackList = feedbackDao.getAllFeedback();
        
        // 3. Send response
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(feedbackList));
    }
}
