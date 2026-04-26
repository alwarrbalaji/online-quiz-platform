<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<% if (session.getAttribute("currentUser") == null) { response.sendRedirect("login.jsp"); return; } %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Quiz in Progress</title>
    <%-- The external stylesheet link can be kept or removed for this test --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <script src="https://unpkg.com/feather-icons"></script>

    <style>r
        .results-summary {
            margin-bottom: 2rem;
            background-color: var(--primary);
            color: var(--text-dark);
            text-align: center;
            padding: 1.5rem;
            border-radius: 12px;
        }
        
        /* Base style for options after submission */
        .submitted .option-label {
            transition: opacity 0.3s ease, background-color 0.3s ease;
        }

        /* Fade out the neutral, unselected options */
        .submitted .option-label:not(.correct-answer):not(.incorrect-answer) {
            opacity: 0.5;
            background-color: #f8f9fa;
        }

        /* Style for the CORRECT answer */
        .submitted .option-label.correct-answer {
            background-color: #d4edda !important; /* Use !important to override other styles */
            border-color: #c3e6cb !important;
            font-weight: 700 !important;
            color: #155724 !important;
        }

        /* Style for the user's INCORRECT choice */
        .submitted .option-label.incorrect-answer {
            background-color: #f8d7da !important;
            border-color: #f5c6cb !important;
            color: #721c24 !important;
            text-decoration: line-through;
        }

        /* Icon positioning */
        .submitted .option-label span { flex-grow: 1; }

        .submitted .option-label.correct-answer::after,
        .submitted .option-label.incorrect-answer::after {
            font-size: 1.5rem;
            font-weight: bold;
            margin-left: 1rem;
        }

        .submitted .option-label.correct-answer::after { content: '✔'; color: #155724; }
        .submitted .option-label.incorrect-answer::after { content: '✖'; color: #721c24; }
    </style>
    </head>
<body>
    <div class="app-container">
        <aside class="sidebar">
            <div class="sidebar-header">
                <h2>LEARN-ED</h2>
            </div>
            <ul class="sidebar-nav">
                <li class="nav-item"><a href="${pageContext.request.contextPath}/dashboard"><i data-feather="grid"></i> Dashboard</a></li>
                <!-- Change this line in all sidebars -->
                <li class="nav-item"><a href="${pageContext.request.contextPath}/jsp/create-course.jsp"><i data-feather="plus-circle"></i> Create Course</a></li>
				<li class="nav-item"><a href="${pageContext.request.contextPath}/my-results"><i data-feather="check-square"></i> My Results</a></li>
				<li class="nav-item"><a href="${pageContext.request.contextPath}/profile"><i data-feather="user"></i> Profile</a></li>            
				<li class="nav-item"><a href="${pageContext.request.contextPath}/rewards"><i data-feather="award"></i> My Rewards</a></li>            
				</ul>
            <div class="sidebar-footer">
                <a href="${pageContext.request.contextPath}/logout"><i data-feather="log-out"></i> Logout</a>
            </div>
        </aside>

        <main class="main-content">
            <header class="main-header">
                <h1>Java Basics Quiz</h1>
            </header>

            <c:if test="${submitted}">
                <div class="card results-summary">
                    <h3>Quiz Complete! Your Score: ${score} / ${totalQuestions}</h3>
                </div>
            </c:if>

            <div class="quiz-container">
                <%-- We add a 'submitted' class to the form after the quiz is graded --%>
                <form action="${pageContext.request.contextPath}/quiz" method="post" class="${submitted ? 'submitted' : ''}">
                    <input type="hidden" name="quizId" value="${quizId}">
                    
                    <c:forEach var="question" items="${questions}" varStatus="loop">
                        <div class="question-block">
                            <p class="question-number">Question ${loop.count}</p>
                            <h3 class="question-text"><c:out value="${question.questionText}" /></h3>
                            
                            <div class="options-group">
                                <c:forEach var="option" items="${question.options}">
                                    <%-- This is a cleaner way to determine the CSS class --%>
                                    <c:set var="optionClass" value=""/>
                                    <c:if test="${submitted}">
                                        <c:choose>
                                            <c:when test="${option.correct}">
                                                <c:set var="optionClass" value="correct-answer"/>
                                            </c:when>
                                            <c:when test="${userAnswers[question.id] == option.id}">
                                                <c:set var="optionClass" value="incorrect-answer"/>
                                            </c:when>
                                        </c:choose>
                                    </c:if>

                                    <label class="option-label ${optionClass}">
                                        <input type="radio" name="question_${question.id}" value="${option.id}" required 
                                            ${userAnswers[question.id] == option.id ? 'checked' : ''} 
                                            ${submitted ? 'disabled' : ''}>
                                        <span><c:out value="${option.optionText}" /></span>
                                    </label>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>
                    
                    <c:if test="${!submitted}">
                        <button type="submit" class="btn">Submit Answers</button>
                    </c:if>
                    <c:if test="${submitted}">
                        <a href="${pageContext.request.contextPath}/dashboard" class="btn">Back to Dashboard</a>
                    </c:if>
                </form>
            </div>
        </main>
    </div>
    <style>
        /* --- Feedback Modal Styles --- */
        .modal {
            display: none;
            position: fixed;
            z-index: 1000;
            left: 0;
            top: 0;
            width: 100%;
            height: 100%;
            background-color: rgba(0,0,0,0.5);
            backdrop-filter: blur(5px);
        }
        .modal-content {
            background-color: white;
            margin: 10% auto;
            padding: 2rem;
            border-radius: 12px;
            width: 90%;
            max-width: 500px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
            position: relative;
        }
        .modal-header h2 { margin-top: 0; color: var(--text-dark); }
        .rating-stars {
            display: flex;
            flex-direction: row-reverse;
            justify-content: center;
            margin: 1.5rem 0;
        }
        .rating-stars input {
            position: absolute;
            opacity: 0;
            pointer-events: none;
        }
        .rating-stars label {
            cursor: pointer;
            width: 40px;
            height: 40px;
            background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='none' stroke='%23ccc' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolygon points='12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2'%3E%3C/polygon%3E%3C/svg%3E");
            background-repeat: no-repeat;
            background-position: center;
            background-size: 32px;
            transition: transform 0.2s;
        }
        .rating-stars label:hover,
        .rating-stars label:hover ~ label,
        .rating-stars input:checked ~ label {
            background-image: url("data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' width='24' height='24' viewBox='0 0 24 24' fill='%23f1c40f' stroke='%23f1c40f' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3E%3Cpolygon points='12 2 15.09 8.26 22 9.27 17 14.14 18.18 21.02 12 17.77 5.82 21.02 7 14.14 2 9.27 8.91 8.26 12 2'%3E%3C/polygon%3E%3C/svg%3E");
        }
        .rating-stars label:hover { transform: scale(1.2); }
        .feedback-textarea {
            width: 100%;
            padding: 0.8rem;
            border: 1px solid #ddd;
            border-radius: 8px;
            margin-bottom: 1.5rem;
            font-family: inherit;
            resize: vertical;
        }
        .modal-actions { display: flex; gap: 1rem; justify-content: flex-end; }
    </style>

    <%-- Feedback Modal --%>
    <div id="feedbackModal" class="modal">
        <div class="modal-content">
            <div class="modal-header">
                <h2>Rate this Quiz</h2>
                <p>How was your experience with <strong>${subject != null ? subject : 'this quiz'}</strong>?</p>
            </div>
            <form id="feedbackForm">
                <input type="hidden" id="feedbackQuizTitle" value="${subject != null ? subject : 'Java Basics Quiz'}">
                <div class="rating-stars">
                    <input type="radio" id="star5" name="rating" value="5" required/><label for="star5"></label>
                    <input type="radio" id="star4" name="rating" value="4"/><label for="star4"></label>
                    <input type="radio" id="star3" name="rating" value="3"/><label for="star3"></label>
                    <input type="radio" id="star2" name="rating" value="2"/><label for="star2"></label>
                    <input type="radio" id="star1" name="rating" value="1"/><label for="star1"></label>
                </div>
                <textarea id="feedbackComment" class="feedback-textarea" placeholder="Tell us more (optional)..." rows="3"></textarea>
                <div class="modal-actions">
                    <button type="button" class="btn btn-secondary" onclick="closeFeedback()">Skip</button>
                    <button type="submit" class="btn btn-primary">Submit Feedback</button>
                </div>
            </form>
            <div id="feedbackSuccess" style="display:none; text-align:center; padding: 2rem 0;">
                <i data-feather="check-circle" style="width:64px; height:64px; color:var(--primary); margin-bottom:1rem;"></i>
                <h3>Thank You!</h3>
                <p>Your feedback helps us improve.</p>
                <button type="button" class="btn btn-primary" style="margin-top:1rem;" onclick="closeFeedback()">Done</button>
            </div>
        </div>
    </div>

    <%-- Signal to JS if quiz was submitted --%>
    <input type="hidden" id="quizSubmittedSignal" value="${submitted ? 'true' : 'false'}">

    <script>
        feather.replace();

        // Show feedback modal if quiz was just submitted
        window.onload = function() {
            const isSubmitted = document.getElementById('quizSubmittedSignal').value === 'true';
            if (isSubmitted) {
                console.log("Quiz submitted, waiting to show feedback modal...");
                setTimeout(function() {
                    console.log("Showing feedback modal.");
                    document.getElementById('feedbackModal').style.display = 'block';
                }, 3000); // Wait 3 seconds as requested
            }
        };

        function closeFeedback() {
            document.getElementById('feedbackModal').style.display = 'none';
        }

        document.getElementById('feedbackForm').onsubmit = function(e) {
            e.preventDefault();
            const rating = document.querySelector('input[name="rating"]:checked').value;
            const comment = document.getElementById('feedbackComment').value;
            const quizTitle = document.getElementById('feedbackQuizTitle').value;

            const feedbackData = {
                quizTitle: quizTitle,
                rating: parseInt(rating),
                comment: comment
            };

            const apiUrl = '${pageContext.request.contextPath}/api/feedback/submit';
            console.log("Submitting feedback to:", apiUrl, feedbackData);

            fetch(apiUrl, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(feedbackData)
            })
            .then(response => {
                console.log("Response status:", response.status);
                if(response.ok) {
                    document.getElementById('feedbackForm').style.display = 'none';
                    document.getElementById('feedbackSuccess').style.display = 'block';
                    feather.replace();
                } else {
                    response.text().then(text => {
                        console.error("Submission failed:", text);
                        alert('Oops! Submission failed (Status ' + response.status + '). Please check console log.');
                    });
                }
            })
            .catch(error => {
                console.error('Fetch error:', error);
                alert('Connection error. Please check your internet and server status.');
            });
        };
    </script>
</body>
</html>