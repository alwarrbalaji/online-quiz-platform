package com.quiz.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.quiz.dao.QuestionDao;
import com.quiz.dao.QuizDao;
import com.quiz.dao.ResultDao;
import com.quiz.dao.RewardDao;
import com.quiz.model.Option;
import com.quiz.model.Question;
import com.quiz.model.Quiz;
import com.quiz.model.User;

@WebServlet("/quiz")
public class QuizServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private QuestionDao questionDao;
    private QuizDao     quizDao;
    private ResultDao   resultDao;
    private RewardDao   rewardDao;

    @Override
    public void init() {
        questionDao = new QuestionDao();
        quizDao     = new QuizDao();
        resultDao   = new ResultDao();
        rewardDao   = new RewardDao();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Session guard
        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        String quizIdParam = request.getParameter("quizId");
        if (isNullOrEmpty(quizIdParam)) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
            return;
        }

        try {
            int quizId = Integer.parseInt(quizIdParam.trim());
            List<Question> questions = questionDao.getQuestionsByQuizId(quizId);
            Quiz quiz = quizDao.getQuizById(quizId);
            request.setAttribute("questions", questions);
            request.setAttribute("quizId",    quizId);
            request.setAttribute("subject",   quiz != null ? quiz.getSubject() : "Java Basics Quiz");
            // ← FIXED: quiz.jsp is inside jsp/ folder
            request.getRequestDispatcher("/jsp/quiz.jsp")
                   .forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/jsp/login.jsp");
            return;
        }

        int quizId = Integer.parseInt(request.getParameter("quizId"));
        List<Question> questions = questionDao.getQuestionsByQuizId(quizId);

        Map<Integer, Integer> userAnswers       = new HashMap<>();
        Map<Integer, Boolean> answerCorrectness = new HashMap<>();
        int score = scoreQuiz(request, questions, userAnswers, answerCorrectness);

        HttpSession session = request.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("currentUser");
            if (user != null) {
                resultDao.saveResult(user.getId(), quizId, score, questions.size());
                rewardDao.updateQuizStreak(user.getId());
            }
        }

        request.setAttribute("questions",         questions);
        request.setAttribute("quizId",            quizId);
        Quiz quiz = quizDao.getQuizById(quizId);
        request.setAttribute("subject",           quiz != null ? quiz.getSubject() : "Java Basics Quiz");
        request.setAttribute("score",             score);
        request.setAttribute("totalQuestions",    questions.size());
        request.setAttribute("userAnswers",       userAnswers);
        request.setAttribute("answerCorrectness", answerCorrectness);
        request.setAttribute("submitted",         true);

        // ← FIXED: quiz.jsp is inside jsp/ folder
        request.getRequestDispatcher("/jsp/quiz.jsp")
               .forward(request, response);
    }

    private int scoreQuiz(HttpServletRequest request,
                          List<Question> questions,
                          Map<Integer, Integer> userAnswers,
                          Map<Integer, Boolean> answerCorrectness) {
        int score = 0;
        for (Question question : questions) {
            String param = request.getParameter("question_" + question.getId());
            if (param == null) continue;
            int userAnswerId = Integer.parseInt(param);
            userAnswers.put(question.getId(), userAnswerId);
            int correctId = -1;
            for (Option option : question.getOptions()) {
                if (option.isCorrect()) { correctId = option.getId(); break; }
            }
            boolean isCorrect = (userAnswerId == correctId);
            answerCorrectness.put(question.getId(), isCorrect);
            if (isCorrect) score++;
        }
        return score;
    }

    private boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("currentUser") != null;
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}