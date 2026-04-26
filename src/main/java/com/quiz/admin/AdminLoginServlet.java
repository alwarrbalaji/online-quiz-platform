package com.quiz.admin;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/admin/login-process")
public class AdminLoginServlet extends HttpServlet {
    private AdminDao adminDao = new AdminDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("adminUser") != null) {
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
            return;
        }
        req.getRequestDispatcher("/jsp/admin-login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user = req.getParameter("username");
        String pass = req.getParameter("password");

        if (user == null || user.trim().isEmpty() || pass == null || pass.trim().isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/jsp/admin-login.jsp?error=empty");
            return;
        }

        com.quiz.model.User admin = adminDao.validateAdmin(user.trim(), pass);
        
        if (admin != null) {
            // Session fixation protection
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            
            HttpSession session = req.getSession(true);
            session.setAttribute("adminUser", admin);
            session.setMaxInactiveInterval(30 * 60); // 30 mins
            resp.sendRedirect(req.getContextPath() + "/admin/dashboard");
        } else {
            resp.sendRedirect(req.getContextPath() + "/jsp/admin-login.jsp?error=invalid");
        }
    }
}
