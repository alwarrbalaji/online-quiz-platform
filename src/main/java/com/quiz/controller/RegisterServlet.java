package com.quiz.controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.quiz.dao.UserDao;
import com.quiz.model.User;

// This annotation maps the servlet to the URL path "/register"
@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDao userDao;

    // Initialize the UserDao when the servlet is created
    @Override
    public void init() {
        userDao = new UserDao();
    }

    // Handles POST requests from the registration form
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPassword(password); // Again, HASH this in a real project
        userDao.registerUser(newUser);
        
        // After registering, redirect the user to the login page
        response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
    }
}