<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% if (session.getAttribute("currentUser") == null) { response.sendRedirect("login.jsp"); return; } %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>View Course - ${course.title}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://unpkg.com/feather-icons"></script>
</head>
<body>
    <div class="app-container">
        <aside class="sidebar">
            <div class="sidebar-header"><h2>LEARN-ED</h2></div>
            <ul class="sidebar-nav">
                <li class="nav-item"><a href="${pageContext.request.contextPath}/dashboard"><i data-feather="grid"></i> Dashboard</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/jsp/create-course.jsp"><i data-feather="plus-circle"></i> Create Course</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/my-results"><i data-feather="check-square"></i> My Results</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/profile"><i data-feather="user"></i> Profile</a></li>
            </ul>
            <div class="sidebar-footer"><a href="${pageContext.request.contextPath}/logout"><i data-feather="log-out"></i> Logout</a></div>
        </aside>

        <main class="main-content">
            <header class="main-header">
                <h1>${course.title}</h1>
                <p>${course.introduction}</p>
            </header>

            <div class="card-container">
                <c:forEach var="lesson" items="${course.lessons}">
                    <div class="card lesson-card">
                        <h3 class="card-title">${lesson.title}</h3>
                        <p>${lesson.material}</p>
                    </div>
                </c:forEach>
            </div>

            <hr>
<div class="quiz-section">
    <div class="card">
        <h3 class="card-title">Test Your Knowledge</h3>
        <p class="card-description">Take a short quiz or save the course material as a PDF.</p>
        
        <div class="action-buttons">
            <a href="${pageContext.request.contextPath}/quiz?quizId=${course.quizId}" class="btn">Start Quiz</a>

            <a href="${pageContext.request.contextPath}/downloadCourse?id=${course.id}" class="btn" style="background-color: #6c757d; color: white; margin-left: 10px;">
    Download as PDF
</a>
        </div>
    </div>
</div>
</main>
    </div>
    <script>
      feather.replace();
    </script>
</body>
</html>