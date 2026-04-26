<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<% if (session.getAttribute("currentUser") == null) { response.sendRedirect("login.jsp"); return; } %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Rewards - LEARN-ED</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Rewards.css?v=2">
    <script src="https://unpkg.com/feather-icons"></script>
    <script src="https://cdn.jsdelivr.net/npm/canvas-confetti@1.6.0/dist/confetti.browser.min.js"></script>
</head>
<body>
<div class="app-container">

    <%-- SIDEBAR --%>
    <aside class="sidebar">
        <div class="sidebar-header"><h2>LEARN-ED</h2></div>
        <ul class="sidebar-nav">
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/dashboard">
                    <i data-feather="grid"></i> Dashboard
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/jsp/create-course.jsp">
                    <i data-feather="plus-circle"></i> Create Course
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/my-results">
                    <i data-feather="check-square"></i> My Results
                </a>
            </li>
            <li class="nav-item">
                <a href="${pageContext.request.contextPath}/profile">
                    <i data-feather="user"></i> Profile
                </a>
            </li>
            <li class="nav-item active">
                <a href="${pageContext.request.contextPath}/rewards">
                    <i data-feather="award"></i> My Rewards
                </a>
            </li>
        </ul>
        <div class="sidebar-footer">
            <a href="${pageContext.request.contextPath}/logout">
                <i data-feather="log-out"></i> Logout
            </a>
        </div>
    </aside>

    <%-- MAIN CONTENT --%>
    <main class="main-content">

        <%-- PAGE HEADER --%>
        <header class="main-header rewards-header">
            <div>
                <h1>🏆 My Rewards</h1>
                <p>Keep learning daily to unlock levels and earn badges!</p>
            </div>
            <div class="header-username">@<c:out value="${currentUser.username}"/></div>
        </header>

        <%-- STREAK OVERVIEW CARDS --%>
        <section class="streak-overview">

            <%-- Login Streak Card --%>
            <div class="streak-card">
                <div class="streak-flame">🔥</div>
                <div class="streak-info">
                    <span class="streak-count">${streak.currentStreak}</span>
                    <span class="streak-label">Day Login Streak</span>
                </div>
                <div class="streak-meta">
                    <span class="streak-best">Best: ${streak.longestStreak} days</span>
                    <c:choose>
                        <c:when test="${streak.daysToNextLoginLevel == 0}">
                            <span class="streak-next gold-text">🥇 MAX LEVEL!</span>
                        </c:when>
                        <c:otherwise>
                            <span class="streak-next">
                                ${streak.daysToNextLoginLevel} more day(s) to next level
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

            <%-- Quiz Streak Card --%>
            <div class="streak-card">
                <div class="streak-flame">⚡</div>
                <div class="streak-info">
                    <span class="streak-count">${streak.quizStreak}</span>
                    <span class="streak-label">Day Quiz Streak</span>
                </div>
                <div class="streak-meta">
                    <span class="streak-best">Complete a quiz daily!</span>
                    <c:choose>
                        <c:when test="${streak.daysToNextQuizLevel == 0}">
                            <span class="streak-next gold-text">🥇 MAX LEVEL!</span>
                        </c:when>
                        <c:otherwise>
                            <span class="streak-next">
                                ${streak.daysToNextQuizLevel} more day(s) to next level
                            </span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>

        </section>

        <%-- ═══════════════════════════════════════════════ --%>
        <%-- LOGIN STREAK LEVELS                             --%>
        <%-- ═══════════════════════════════════════════════ --%>
        <section class="rewards-section">
            <h2 class="section-title">🔥 Login Streak Levels</h2>
            <p class="section-subtitle">Log in every day to climb the ranks</p>
            <div class="level-track">

                <%-- BRONZE LOGIN --%>
                <c:choose>
                    <c:when test="${loginBronzeEarned}">
                        <div class="level-card earned">
                    </c:when>
                    <c:otherwise>
                        <div class="level-card locked">
                    </c:otherwise>
                </c:choose>
                    <div class="level-medal">🥉</div>
                    <div class="level-body">
                        <h3 class="level-name">Bronze</h3>
                        <p class="level-req">Login for 3 consecutive days</p>
                        <div class="level-progress-bar">
                            <div class="level-progress-fill bronze-fill"
                                 data-current="${streak.currentStreak}"
                                 data-target="3"></div>
                        </div>
                        <span class="level-progress-text">
                            <c:choose>
                                <c:when test="${streak.currentStreak >= 3}">3</c:when>
                                <c:otherwise>${streak.currentStreak}</c:otherwise>
                            </c:choose>
                            / 3 days
                        </span>
                    </div>
                    <div class="level-status">
                        <c:choose>
                            <c:when test="${loginBronzeEarned}">
                                <span class="badge-earned">✔ Earned</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-locked">🔒 Locked</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="level-connector"></div>

                <%-- SILVER LOGIN --%>
                <c:choose>
                    <c:when test="${loginSilverEarned}">
                        <div class="level-card earned">
                    </c:when>
                    <c:otherwise>
                        <div class="level-card locked">
                    </c:otherwise>
                </c:choose>
                    <div class="level-medal">🥈</div>
                    <div class="level-body">
                        <h3 class="level-name">Silver</h3>
                        <p class="level-req">Login for 7 consecutive days</p>
                        <div class="level-progress-bar">
                            <div class="level-progress-fill silver-fill"
                                 data-current="${streak.currentStreak}"
                                 data-target="7"></div>
                        </div>
                        <span class="level-progress-text">
                            <c:choose>
                                <c:when test="${streak.currentStreak >= 7}">7</c:when>
                                <c:otherwise>${streak.currentStreak}</c:otherwise>
                            </c:choose>
                            / 7 days
                        </span>
                    </div>
                    <div class="level-status">
                        <c:choose>
                            <c:when test="${loginSilverEarned}">
                                <span class="badge-earned">✔ Earned</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-locked">🔒 Locked</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="level-connector"></div>

                <%-- GOLD LOGIN --%>
                <c:choose>
                    <c:when test="${loginGoldEarned}">
                        <div class="level-card earned">
                    </c:when>
                    <c:otherwise>
                        <div class="level-card locked">
                    </c:otherwise>
                </c:choose>
                    <div class="level-medal">🥇</div>
                    <div class="level-body">
                        <h3 class="level-name">Gold</h3>
                        <p class="level-req">Login for 30 consecutive days</p>
                        <div class="level-progress-bar">
                            <div class="level-progress-fill gold-fill"
                                 data-current="${streak.currentStreak}"
                                 data-target="30"></div>
                        </div>
                        <span class="level-progress-text">
                            <c:choose>
                                <c:when test="${streak.currentStreak >= 30}">30</c:when>
                                <c:otherwise>${streak.currentStreak}</c:otherwise>
                            </c:choose>
                            / 30 days
                        </span>
                    </div>
                    <div class="level-status">
                        <c:choose>
                            <c:when test="${loginGoldEarned}">
                                <span class="badge-earned">✔ Earned</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-locked">🔒 Locked</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

            </div>
        </section>

        <%-- ═══════════════════════════════════════════════ --%>
        <%-- QUIZ STREAK LEVELS                              --%>
        <%-- ═══════════════════════════════════════════════ --%>
        <section class="rewards-section">
            <h2 class="section-title">⚡ Quiz Streak Levels</h2>
            <p class="section-subtitle">Complete at least one quiz every day to level up</p>
            <div class="level-track">

                <%-- BRONZE QUIZ --%>
                <c:choose>
                    <c:when test="${quizBronzeEarned}">
                        <div class="level-card earned">
                    </c:when>
                    <c:otherwise>
                        <div class="level-card locked">
                    </c:otherwise>
                </c:choose>
                    <div class="level-medal">🥉</div>
                    <div class="level-body">
                        <h3 class="level-name">Bronze</h3>
                        <p class="level-req">Complete quizzes for 3 consecutive days</p>
                        <div class="level-progress-bar">
                            <div class="level-progress-fill bronze-fill"
                                 data-current="${streak.quizStreak}"
                                 data-target="3"></div>
                        </div>
                        <span class="level-progress-text">
                            <c:choose>
                                <c:when test="${streak.quizStreak >= 3}">3</c:when>
                                <c:otherwise>${streak.quizStreak}</c:otherwise>
                            </c:choose>
                            / 3 days
                        </span>
                    </div>
                    <div class="level-status">
                        <c:choose>
                            <c:when test="${quizBronzeEarned}">
                                <span class="badge-earned">✔ Earned</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-locked">🔒 Locked</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="level-connector"></div>

                <%-- SILVER QUIZ --%>
                <c:choose>
                    <c:when test="${quizSilverEarned}">
                        <div class="level-card earned">
                    </c:when>
                    <c:otherwise>
                        <div class="level-card locked">
                    </c:otherwise>
                </c:choose>
                    <div class="level-medal">🥈</div>
                    <div class="level-body">
                        <h3 class="level-name">Silver</h3>
                        <p class="level-req">Complete quizzes for 7 consecutive days</p>
                        <div class="level-progress-bar">
                            <div class="level-progress-fill silver-fill"
                                 data-current="${streak.quizStreak}"
                                 data-target="7"></div>
                        </div>
                        <span class="level-progress-text">
                            <c:choose>
                                <c:when test="${streak.quizStreak >= 7}">7</c:when>
                                <c:otherwise>${streak.quizStreak}</c:otherwise>
                            </c:choose>
                            / 7 days
                        </span>
                    </div>
                    <div class="level-status">
                        <c:choose>
                            <c:when test="${quizSilverEarned}">
                                <span class="badge-earned">✔ Earned</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-locked">🔒 Locked</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <div class="level-connector"></div>

                <%-- GOLD QUIZ --%>
                <c:choose>
                    <c:when test="${quizGoldEarned}">
                        <div class="level-card earned">
                    </c:when>
                    <c:otherwise>
                        <div class="level-card locked">
                    </c:otherwise>
                </c:choose>
                    <div class="level-medal">🥇</div>
                    <div class="level-body">
                        <h3 class="level-name">Gold</h3>
                        <p class="level-req">Complete quizzes for 30 consecutive days</p>
                        <div class="level-progress-bar">
                            <div class="level-progress-fill gold-fill"
                                 data-current="${streak.quizStreak}"
                                 data-target="30"></div>
                        </div>
                        <span class="level-progress-text">
                            <c:choose>
                                <c:when test="${streak.quizStreak >= 30}">30</c:when>
                                <c:otherwise>${streak.quizStreak}</c:otherwise>
                            </c:choose>
                            / 30 days
                        </span>
                    </div>
                    <div class="level-status">
                        <c:choose>
                            <c:when test="${quizGoldEarned}">
                                <span class="badge-earned">✔ Earned</span>
                            </c:when>
                            <c:otherwise>
                                <span class="badge-locked">🔒 Locked</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

            </div>
        </section>

        <%-- MOTIVATION BANNER --%>
        <div class="motivation-banner">
            <span class="motivation-emoji">💡</span>
            <div>
                <strong>Keep your streak alive!</strong>
                <p>Log in and complete at least one quiz every day to level up faster.</p>
            </div>
            <a href="${pageContext.request.contextPath}/dashboard" class="btn">
                Go Practice →
            </a>
        </div>

    </main>
</div>

<script>feather.replace();</script>
<script>
document.addEventListener("DOMContentLoaded", function () {
    // Calculate and animate progress bars on load
    document.querySelectorAll(".level-progress-fill").forEach(function (fill) {
        var current = parseInt(fill.getAttribute("data-current")) || 0;
        var target = parseInt(fill.getAttribute("data-target")) || 1;
        
        // Calculate width based on target level
        var targetWidth;
        if (current >= target) {
            targetWidth = 100;
        } else if (target === 3) {
            targetWidth = current * 33;
        } else if (target === 7) {
            targetWidth = current >= 3 ? (current * 14) : 0;
        } else if (target === 30) {
            targetWidth = current >= 7 ? (current * 3) : 0;
        } else {
            targetWidth = (current / target) * 100;
        }
        
        fill.style.width = "0%";
        setTimeout(function () {
            fill.style.transition = "width 0.8s cubic-bezier(0.4,0,0.2,1)";
            fill.style.width = targetWidth + "%";
        }, 200);
    });
    // Animate earned cards with pop effect
    document.querySelectorAll(".level-card.earned").forEach(function (card, i) {
        card.style.opacity = "0";
        card.style.transform = "translateY(20px)";
        setTimeout(function () {
            card.style.transition = "opacity 0.4s ease, transform 0.4s ease";
            card.style.opacity = "1";
            card.style.transform = "translateY(0)";
        }, 100 + i * 120);
    });

    // Fire confetti if the user has earned any badges!
    var hasEarnedBadges = "${loginBronzeEarned || loginSilverEarned || loginGoldEarned || quizBronzeEarned || quizSilverEarned || quizGoldEarned}" === "true";
    if (hasEarnedBadges) {
        setTimeout(function() {
            confetti({
                particleCount: 150,
                spread: 80,
                origin: { y: 0.6 },
                colors: ['#2ecc71', '#f1c40f', '#3498db', '#e74c3c']
            });
        }, 800);
    }
});
</script>
</body>
</html>