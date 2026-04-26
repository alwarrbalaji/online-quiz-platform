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

@WebServlet("/downloadCourse")
public class DownloadCourseServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final CourseDao courseDao = new CourseDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 1. Get the course ID from the URL
            int courseId = Integer.parseInt(request.getParameter("id"));

            // 2. Fetch the course data from the database
            Course course = courseDao.getCourseById(courseId);

            // 3. Pass the course data to the new printable JSP page
            request.setAttribute("course", course);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/jsp/course-printable.jsp");
            dispatcher.forward(request, response);

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }
}