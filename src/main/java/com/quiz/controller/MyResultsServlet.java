package com.quiz.controller;

import com.quiz.dao.ResultDao;
import com.quiz.model.Result;
import com.quiz.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/my-results")
public class MyResultsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final ResultDao resultDao = new ResultDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        User currentUser = (session != null) ? (User) session.getAttribute("currentUser") : null;

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        List<Result> resultsList = resultDao.getResultsByUserId(currentUser.getId());
        request.setAttribute("resultsList", resultsList);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/my-results.jsp");
        dispatcher.forward(request, response);
    }
}