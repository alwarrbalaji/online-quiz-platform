<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% if (session.getAttribute("currentUser") == null) { response.sendRedirect("login.jsp"); return; } %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Results</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://unpkg.com/feather-icons"></script>

    <style>
        .results-list {
            display: flex;
            flex-direction: column;
            gap: 1rem;
        }
        .result-card {
            display: grid;
            grid-template-columns: 2fr 1fr 1fr;
            align-items: center;
            background-color: var(--white);
            padding: 1rem 1.5rem;
            border-radius: 8px;
            box-shadow: 0 2px 4px var(--shadow);
            transition: transform 0.2s ease, box-shadow 0.2s ease;
        }
        .result-card:hover {
            transform: translateY(-3px);
            box-shadow: 0 4px 8px var(--shadow);
        }
        .result-subject {
            display: flex;
            align-items: center;
            font-weight: 500;
        }
        .result-subject svg {
            margin-right: 1rem;
            width: 20px;
            color: var(--primary);
        }
        .result-score {
            font-weight: 700;
            text-align: center;
        }
        .result-date {
            font-size: 0.9rem;
            color: #999;
            text-align: right;
        }
    </style>
</head>
<body>
    <div class="app-container">
        <aside class="sidebar">
            <div class="sidebar-header"><h2>LEARN-ED</h2></div>
            <ul class="sidebar-nav">
                <li class="nav-item"><a href="${pageContext.request.contextPath}/dashboard"><i data-feather="grid"></i> Dashboard</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/jsp/create-course.jsp"><i data-feather="plus-circle"></i> Create Course</a></li>
                <li class="nav-item active"><a href="${pageContext.request.contextPath}/my-results"><i data-feather="check-square"></i> My Results</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/profile"><i data-feather="user"></i> Profile</a></li>
                <li class="nav-item"><a href="${pageContext.request.contextPath}/rewards"><i data-feather="award"></i> My Rewards</a></li>
            </ul>
            <div class="sidebar-footer"><a href="${pageContext.request.contextPath}/logout"><i data-feather="log-out"></i> Logout</a></div>
        </aside>

        <main class="main-content">
            <header class="main-header">
                <h1>My Results</h1>
                <p>A history of all the quizzes you have completed.</p>
            </header>

            <div class="results-list">
                <c:forEach var="result" items="${resultsList}">
                    <div class="result-card">
                        <div class="result-subject">
                            <i data-feather="book-open"></i>
                            <span><c:out value="${result.quizSubject}" /></span>
                        </div>
                        <div class="result-score">
                            <strong>${result.score} / ${result.totalQuestions}</strong>
                        </div>
                        <div class="result-date">
                            ${result.formattedDate}
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${empty resultsList}">
                    <p>You have not completed any quizzes yet.</p>
                </c:if>
            </div>
        </main>
    </div>
    <script>
      feather.replace();
    </script>
</body>
</html>