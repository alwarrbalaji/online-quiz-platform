package com.quiz.controller;

//--- Add these missing imports ---
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // For HttpSession
import com.quiz.dao.ResultDao;         // For ResultDao
import com.quiz.dao.UserDao;           // For UserDao
import com.quiz.model.PerformanceStats; // For PerformanceStats
import com.quiz.model.Profile;          // For Profile
import com.quiz.model.User;           // For User
import java.io.IOException;
//--- End of new imports ---

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDao userDao = new UserDao();

 // In com.quiz.controller.ProfileServlet.java

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        
        // This is the line that was likely missing or misplaced.
        // We MUST declare and initialize currentUser before we can use it.
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        // Security check: If the user isn't logged in, redirect them.
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return; // Stop further execution
        }

        // Now we can safely use currentUser
        Profile profile = userDao.getUserProfile(currentUser.getId());
        
        ResultDao resultDao = new ResultDao();
        PerformanceStats stats = resultDao.getUserPerformanceStats(currentUser.getId());
        
        request.setAttribute("userProfile", profile);
        request.setAttribute("user", currentUser); // Pass the main user object too
        request.setAttribute("userStats", stats); // Pass the new stats to the JSP

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/profile.jsp");
        dispatcher.forward(request, response);
    }
    // Handles saving the updated profile
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        String fullName = request.getParameter("fullName");
        String bio = request.getParameter("bio");

        Profile profile = new Profile();
        profile.setUserId(currentUser.getId());
        profile.setFullName(fullName);
        profile.setBio(bio);

        userDao.updateUserProfile(profile);

        response.sendRedirect("profile"); // Redirect back to the profile page
    }
}