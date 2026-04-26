package com.quiz.model;

import java.time.LocalDateTime;

public class Feedback {

    private Long id;
    private String username;
    private String quizTitle;
    private Integer rating;
    private String comment;
    private String submittedAt;

    // Constructors
    public Feedback() {}

    public Feedback(String username, String quizTitle, Integer rating, String comment, String submittedAt) {
        this.username = username;
        this.quizTitle = quizTitle;
        this.rating = rating;
        this.comment = comment;
        this.submittedAt = submittedAt;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
}
