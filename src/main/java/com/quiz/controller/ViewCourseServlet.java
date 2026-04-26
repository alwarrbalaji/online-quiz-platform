package com.quiz.controller;

import com.quiz.dao.CourseDao;
import com.quiz.model.Course;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/viewCourse")
public class ViewCourseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private CourseDao courseDao = new CourseDao();

    /**
     * Handles requests to view a single course.
     * It retrieves the course ID from the request parameter, fetches the course data
     * from the database, and forwards it to the view-course.jsp page.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 1. Get the course ID from the URL parameter (e.g., ?id=1)
            int courseId = Integer.parseInt(request.getParameter("id"));
            
            // 2. Fetch the complete course object (with its lessons) from the DAO
            Course course = courseDao.getCourseById(courseId);
            
            // 3. Set the course object as an attribute to be accessed by the JSP
            request.setAttribute("course", course);
            
            // 4. Forward the request and the data to the JSP page for display
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/view-course.jsp");
            dispatcher.forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/dashboard"); // Redirect to dashboard on error
        }
    }
}