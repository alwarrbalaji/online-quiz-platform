<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% if (session.getAttribute("currentUser") == null) { response.sendRedirect("login.jsp"); return; } %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create a New Course</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://unpkg.com/feather-icons"></script>
</head>
<body>
    <div class="app-container">
        <aside class="sidebar">
            <div class="sidebar-header"><h2>LEARN-ED</h2></div>
            <ul class="sidebar-nav">
                <li class="nav-item"><a href="${pageContext.request.contextPath}/dashboard"><i data-feather="grid"></i> Dashboard</a></li>
                <li class="nav-item active"><a href="${pageContext.request.contextPath}/jsp/create-course.jsp"><i data-feather="plus-circle"></i> Create Course</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/my-results"><i data-feather="check-square"></i> My Results</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/profile"><i data-feather="user"></i> Profile</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/rewards"><i data-feather="award"></i> My Rewards</a></li>
            </ul>
            <div class="sidebar-footer"><a href="${pageContext.request.contextPath}/logout"><i data-feather="log-out"></i> Logout</a></div>
        </aside>

        <main class="main-content">
            <header class="main-header">
                <h1>Create a New Course</h1>
                <p>Provide a topic and let GenAI create personalized learning materials for you.</p>
            </header>

            <div class="card">
                <form action="${pageContext.request.contextPath}/createCourse" method="post">
                    <div class="form-group">
                        <label for="topic">Course Topic</label>
                        <input type="text" id="topic" name="topic" class="form-control" placeholder="e.g., Introduction to Python, SQL for Beginners" required>
                    </div>
                    <div class="form-group">
                        <label for="level">Learning Level</label>
                        <select id="level" name="level" class="form-control">
                            <option value="Beginner">Beginner</option>
                            <option value="Intermediate">Intermediate</option>
                            <option value="Advanced">Advanced</option>
                        </select>
                    </div>
                    <button type="submit" class="btn">Generate Course And Quizzes</button>
                </form>
            </div>
        </main>
    </div>
    <script>feather.replace();</script>
</body>
</html>