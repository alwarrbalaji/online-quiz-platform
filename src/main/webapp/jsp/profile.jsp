<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% if (session.getAttribute("currentUser") == null) { response.sendRedirect("login.jsp"); return; } %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Profile</title>
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
                <li class="nav-item active"><a href="${pageContext.request.contextPath}/profile"><i data-feather="user"></i> Profile</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/rewards"><i data-feather="award"></i> My Rewards</a></li>
            </ul>
            <div class="sidebar-footer"><a href="${pageContext.request.contextPath}/logout"><i data-feather="log-out"></i> Logout</a></div>
        </aside>

        <main class="main-content">
            <header class="main-header">
                <h1>My Profile</h1>
                <p>View and update your personal information.</p>
            </header>

            <div class="card">
                <form action="${pageContext.request.contextPath}/profile" method="post">
                    <div class="form-group">
                        <label>Username</label>
                        <input type="text" class="form-control" value="${user.username}" disabled>
                    </div>
                    <div class="form-group">
                        <label>Email</label>
                        <input type="email" class="form-control" value="${user.email}" disabled>
                    </div>
                    <hr>
                    <div class="form-group">
                        <label for="fullName">Full Name</label>
                        <input type="text" id="fullName" name="fullName" class="form-control" value="${userProfile.fullName}">
                    </div>
                    <div class="form-group">
                        <label for="bio">Bio</label>
                        <textarea id="bio" name="bio" class="form-control" rows="4">${userProfile.bio}</textarea>
                    </div>
                    <button type="submit" class="btn">Save Changes</button>
                </form>
            </div>
            <section class="dashboard-section">
    <h2 class="section-title">Performance Overview</h2>
    <div class="stats-container">
        <div class="chart-card card">
            <canvas id="performanceChart"></canvas>
        </div>
        <div class="summary-card card">
            <h4>Summary</h4>
            <div class="summary-item">
                <span>Quizzes Taken</span>
                <strong>${userStats.quizzesTaken}</strong>
            </div>
            <div class="summary-item">
                <span>Overall Score</span>
                <strong><c:out value="${String.format('%.0f%%', userStats.averageScorePercentage)}"/></strong>
            </div>
            <div class="summary-item">
                <span>Correct Answers</span>
                <strong>${userStats.totalCorrectAnswers}</strong>
            </div>
        </div>
    </div>
</section>
        </main>
    </div>
   <script>
    feather.replace();

    // Data from our servlet
    const correctAnswers = Number("${userStats != null ? userStats.totalCorrectAnswers : 0}");
    const totalQuestions = Number("${userStats != null ? userStats.totalQuestionsAnswered : 0}");
    const incorrectAnswers = totalQuestions - correctAnswers;

    // Chart configuration
    const ctx = document.getElementById('performanceChart').getContext('2d');
    const performanceChart = new Chart(ctx, {
        type: 'doughnut', // A nice modern chart type
        data: {
            labels: ['Correct Answers', 'Incorrect Answers'],
            datasets: [{
                label: 'Answer Breakdown',
                data: [correctAnswers, incorrectAnswers],
                backgroundColor: [
                    '#BCD39C', // Your primary color
                    '#f8d7da'  // The error color
                ],
                borderColor: [
                    '#FFFFFF',
                    '#FFFFFF'
                ],
                borderWidth: 3
            }]
        },
        options: {
            responsive: true,
            plugins: {
                legend: {
                    position: 'top',
                },
                title: {
                    display: true,
                    text: 'Answer Correctness Breakdown'
                }
            }
        }
    });
</script>
</body>
</html>