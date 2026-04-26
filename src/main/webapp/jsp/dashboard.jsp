<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% if (session.getAttribute("currentUser") == null) { response.sendRedirect("login.jsp"); return; } %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - LEARN-ED</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Rewards.css">
    <script src="https://unpkg.com/feather-icons"></script>
</head>
<body>
<div class="app-container">

    <aside class="sidebar">
        <div class="sidebar-header"><h2>LEARN-ED</h2></div>
        <ul class="sidebar-nav">
            <li class="nav-item active">
                <a href="${pageContext.request.contextPath}/dashboard">
                    <i data-feather="grid"></i> Dashboard</a></li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/jsp/create-course.jsp">
                    <i data-feather="plus-circle"></i> Create Course</a></li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/my-results">
                    <i data-feather="check-square"></i> My Results</a></li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/profile">
                    <i data-feather="user"></i> Profile</a></li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/rewards">
                    <i data-feather="award"></i> My Rewards</a></li>
        </ul>
        <div class="sidebar-footer">
            <a href="${pageContext.request.contextPath}/logout">
                <i data-feather="log-out"></i> Logout</a>
        </div>
    </aside>

    <main class="main-content">

        <header class="main-header">
            <h1>Welcome, <c:out value="${currentUser.username}"/>! 👋</h1>
            <p>Select a quiz to test your knowledge or create a new course.</p>
        </header>

        <%-- STREAK WIDGET --%>
        <c:if test="${streak != null}">
            <section class="streak-overview" style="margin-top:1.5rem;">
                <div class="streak-card">
                    <div class="streak-flame">🔥</div>
                    <div class="streak-info">
                        <span class="streak-count">${streak.currentStreak}</span>
                        <span class="streak-label">Day Login Streak</span>
                    </div>
                    <div class="streak-meta">
                        <span class="streak-best">Level: ${streak.loginLevel}</span>
                        <a href="${pageContext.request.contextPath}/rewards"
                           class="btn"
                           style="padding:0.4rem 1rem; font-size:0.82rem;">
                            View Rewards →
                        </a>
                    </div>
                </div>
                <div class="streak-card">
                    <div class="streak-flame">⚡</div>
                    <div class="streak-info">
                        <span class="streak-count">${streak.quizStreak}</span>
                        <span class="streak-label">Day Quiz Streak</span>
                    </div>
                    <div class="streak-meta">
                        <span class="streak-best">Level: ${streak.quizLevel}</span>
                    </div>
                </div>
            </section>
        </c:if>

        <%-- MY COURSES --%>
        <section class="dashboard-section">
            <h2 class="section-title">📘 My Courses</h2>
            <div class="card-container">
                <c:forEach var="course" items="${userCourses}">
                    <div class="card course-card">
                        <div class="card-icon-container">
                            <i data-feather="book"></i>
                        </div>
                        <div class="card-content">
                            <h3 class="card-title"><c:out value="${course.title}"/></h3>
                            <p class="card-description">AI-generated course material and quiz.</p>
                            <span class="card-status">Status: Not Started</span>
                        </div>
                        <div class="card-actions">
                            <a href="${pageContext.request.contextPath}/viewCourse?id=${course.id}"
                               class="btn">View Course →</a>
                        </div>
                    </div>
                </c:forEach>
                <c:if test="${empty userCourses}">
                    <p>You haven't generated any courses yet.</p>
                </c:if>
            </div>
        </section>

        <%-- PLACEMENT QUIZZES --%>
        <section class="dashboard-section">
            <h2 class="section-title">📝 Placement Practice Quizzes</h2>
            <c:forEach var="categoryEntry" items="${quizzesByCategory}">
                <h3 class="category-subtitle">
                    <c:out value="${categoryEntry.key}"/>
                </h3>
                <div class="card-container">
                    <c:forEach var="quiz" items="${categoryEntry.value}">
                        <div class="card quiz-card">
                            <div class="card-icon-container">
                                <i data-feather="file-text"></i>
                            </div>
                            <div class="card-content">
                                <h3 class="card-title"><c:out value="${quiz.subject}"/></h3>
                                <p class="card-description">
                                    A series of pre-made questions on this topic.
                                </p>
                            </div>
                            <div class="card-actions">
                                <a href="${pageContext.request.contextPath}/quiz?quizId=${quiz.id}"
                                   class="btn btn-secondary">Start Quiz →</a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:forEach>
        </section>

    </main>
</div>

<script>feather.replace();</script>
</body>
</html>