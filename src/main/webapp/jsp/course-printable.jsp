<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% if (session.getAttribute("currentUser") == null) { response.sendRedirect(request.getContextPath() + "/jsp/login.jsp"); return; } %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Print - ${course.title}</title>
    <style>
        body { font-family: sans-serif; line-height: 1.6; padding: 2rem; }
        h1 { font-size: 24px; margin-bottom: 0.5rem; }
        p.intro { font-style: italic; color: #555; margin-bottom: 2rem; }
        h2 { font-size: 18px; margin-top: 1.5rem; margin-bottom: 0.5rem; border-bottom: 1px solid #eee; padding-bottom: 0.5rem;}
    </style>
</head>
<body onload="window.print()">

    <h1>${course.title}</h1>
    <p class="intro">${course.introduction}</p>

    <c:forEach var="lesson" items="${course.lessons}">
        <h2>${lesson.title}</h2>
        <p>${lesson.material}</p>
    </c:forEach>

</body>
</html>