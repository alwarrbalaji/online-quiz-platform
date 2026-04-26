<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - ZGen</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<nav class="navbar navbar-expand-lg navbar-dark bg-dark mb-4">
    <div class="container-fluid">
        <a class="navbar-brand" href="#">ZGen Admin</a>
        <div class="d-flex">
            <span class="navbar-text me-3">Welcome, <c:out value="${pageContext.request.userPrincipal.name}"/></span>
            <form action="${pageContext.request.contextPath}/admin/logout" method="post" class="d-inline">
                <button type="submit" class="btn btn-outline-light btn-sm">Logout</button>
            </form>
        </div>
    </div>
</nav>

<div class="container">
    <h2 class="mb-4">Admin Dashboard</h2>

    <div class="card mb-4 shadow-sm">
        <div class="card-header bg-primary text-white">
            <h5 class="mb-0">Currently Active Users</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead>
                        <tr>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Login Timestamp</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="active" items="${activeUsers}">
                            <tr>
                                <td><c:out value="${active.username}"/></td>
                                <td><c:out value="${active.email}"/></td>
                                <td><fmt:formatDate value="${active.loginTime}" pattern="yyyy-MM-dd HH:mm:ss" /></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty activeUsers}">
                            <tr><td colspan="3" class="text-center text-muted">No active users currently.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="card-header bg-secondary text-white">
            <h5 class="mb-0">User Management</h5>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-bordered">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Username</th>
                            <th>Email</th>
                            <th>Registered Date</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="user" items="${allUsers}">
                            <tr>
                                <td><c:out value="${user.id}"/></td>
                                <td><c:out value="${user.username}"/></td>
                                <td><c:out value="${user.email}"/></td>
                                <td><fmt:formatDate value="${user.registeredDate}" pattern="yyyy-MM-dd" /></td>
                                <td>
                                    <span class="badge ${user.active ? 'bg-success' : 'bg-danger'}">
                                        ${user.active ? 'Active' : 'Suspended'}
                                    </span>
                                </td>
                                <td>
                                    <button class="btn btn-sm btn-warning" onclick="disableUser('${user.id}')">Suspend</button>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty allUsers}">
                            <tr><td colspan="6" class="text-center text-muted">No users found in the system.</td></tr>
                        </c:if>
                    </tbody>
                </table>
            </div>
        </div>
    </div>

    <!-- Feedback Data Table -->
    <div class="card mt-4 shadow-sm border-info">
        <div class="card-header bg-info text-white d-flex justify-content-between align-items-center">
            <h5 class="mb-0">User Feedback & Ratings</h5>
            <button class="btn btn-sm btn-light" onclick="fetchFeedback()">Refresh Feedback</button>
        </div>
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover align-middle" id="feedbackTable">
                    <thead class="table-light">
                        <tr>
                            <th>Date</th>
                            <th>User</th>
                            <th>Quiz</th>
                            <th>Rating</th>
                            <th>Comment</th>
                        </tr>
                    </thead>
                    <tbody id="feedbackTableBody">
                        <tr><td colspan="5" class="text-center text-muted">Loading feedback...</td></tr>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script>
document.addEventListener('DOMContentLoaded', function() {
    fetchFeedback();
});

function fetchFeedback() {
    const tbody = document.getElementById('feedbackTableBody');
    fetch('${pageContext.request.contextPath}/api/feedback/view-all')
        .then(response => response.json())
        .then(data => {
            tbody.innerHTML = '';
            if (!data || data.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="text-center text-muted">No feedback submitted yet.</td></tr>';
                return;
            }
            data.forEach(fb => {
                const date = fb.submittedAt || 'N/A';
                const rating = fb.rating || 0;
                const stars = '⭐'.repeat(rating);
                const row = `
                    <tr>
                        <td><small>\${date}</small></td>
                        <td><strong>\${fb.username || 'Anonymous'}</strong></td>
                        <td>\${fb.quizTitle || 'Unknown'}</td>
                        <td class="text-warning">\${stars} (\${rating}/5)</td>
                        <td><i class="text-muted italic">"\${fb.comment || 'No comment'}"</i></td>
                    </tr>
                `;
                tbody.innerHTML += row;
            });
        })
        .catch(error => {
            console.error('Error fetching feedback:', error);
            tbody.innerHTML = '<tr><td colspan="5" class="text-center text-danger">Error loading feedback data.</td></tr>';
        });
}

function disableUser(userId) {
    if(confirm('Are you sure you want to suspend this user?')) {
        let formData = new URLSearchParams();
        formData.append('userId', userId);
        
        fetch('${pageContext.request.contextPath}/admin/api/users/disable', {
            method: 'POST',
            headers: {'Content-Type': 'application/x-www-form-urlencoded'},
            body: formData
        }).then(response => {
            if(response.ok) {
                alert('User suspended successfully');
                window.location.reload();
            } else {
                alert('Failed to suspend user');
            }
        });
    }
}
</script>
</body>
</html>
