package com.quiz.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/dashboard")
public class AdminDashboardServlet extends HttpServlet {
    private AdminDao adminDao = new AdminDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession().getAttribute("adminUser") == null) {
            resp.sendRedirect(req.getContextPath() + "/jsp/admin-login.jsp?error=unauthorized");
            return;
        }
        req.setAttribute("allUsers", adminDao.getAllUsers());
        req.setAttribute("activeUsers", ActiveUserManager.getActiveUsers());
        req.getRequestDispatcher("/jsp/admin-dashboard.jsp").forward(req, resp);
    }
}
