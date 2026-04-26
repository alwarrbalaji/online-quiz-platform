<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - LEARN-ED</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="auth-body">

    <div class="card auth-card">
        <h2 class="card-title text-center">Welcome Back! 👋</h2>
        <p style="text-align:center; color:var(--text-light); margin-bottom:1.5rem;">
            Sign in to continue your learning journey
        </p>

        <%-- error=1 → wrong credentials, error=2 → empty fields --%>
        <% if ("1".equals(request.getParameter("error"))) { %>
            <p class="error-message">❌ Invalid username or password. Please try again.</p>
        <% } else if ("2".equals(request.getParameter("error"))) { %>
            <p class="error-message">❌ Username and password cannot be empty.</p>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="post">
            <div class="form-group">
                <label for="username">Username</label>
                <input type="text"
                       id="username"
                       name="username"
                       class="form-control"
                       placeholder="Enter your username"
                       required>
            </div>
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password"
                       id="password"
                       name="password"
                       class="form-control"
                       placeholder="Enter your password"
                       required>
            </div>
            <button type="submit" class="btn" style="width:100%;">
                Sign In →
            </button>
        </form>

        <p style="text-align:center; margin-top:1.5rem; color:var(--text-light);">
            Don't have an account?
            <a href="${pageContext.request.contextPath}/jsp/register.jsp"
               style="color:var(--primary); font-weight:600;">
                Register here
            </a>
        </p>
    </div>

</body>
</html>